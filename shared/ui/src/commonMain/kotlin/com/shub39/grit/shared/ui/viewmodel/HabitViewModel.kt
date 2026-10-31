/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.shared.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.AnalyticsEvent
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.habits.HabitStatusHandler
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.shared.ui.habit.HabitFilter
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class HabitViewModel(
    @Provided private val scheduler: AlarmScheduler,
    @Provided private val repo: HabitRepo,
    @Provided private val datastore: SettingsDatastore,
    @Provided private val analytics: AnalyticsWrapper,
    @Provided private val statusHandler: HabitStatusHandler,
) : ViewModel() {
    private var habitStatusJob: Job? = null
    private var overallAnalyticsJob: Job? = null
    private var observeDatastoreJob: Job? = null
    private var completedHabitsFetchJob: Job? = null
    private var habitCategoryJob: Job? = null

    private val selectedAnalyticsCategory = MutableStateFlow<Long?>(null)

    private val _state = MutableStateFlow(HabitState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                observeDataStore()
                observeHabitStatuses()
                observeHabitCategories()
                observeOverallAnalytics()

                rescheduleAllHabits()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitState())

    // handles actions from habit page
    fun onAction(action: HabitsAction) {
        viewModelScope.launch {
            when (action) {
                is AddHabit -> {
                    analytics.trackEvent(
                        AnalyticsEvent.HABIT_CREATED,
                        mapOf(
                            "has_reminder" to action.habit.reminder,
                            "is_measurable" to action.habit.isMeasurable,
                        ),
                    )
                    upsertHabit(action.habit)
                }

                is DeleteHabit -> {
                    analytics.trackEvent(
                        AnalyticsEvent.HABIT_DELETED,
                        mapOf("has_reminder" to action.habit.reminder),
                    )
                    deleteHabit(action.habit)
                }

                is InsertStatus -> insertHabitStatus(action.habit, action.date)

                is UpdateHabit -> {
                    analytics.trackEvent(
                        AnalyticsEvent.HABIT_EDITED,
                        mapOf("has_reminder" to action.habit.reminder),
                    )
                    upsertHabit(action.habit)
                }

                ReorderHabits -> {
                    val currentList =
                        _state.value.habitsWithAnalytics.mapIndexed { index, analytics ->
                            analytics.habit.copy(index = index)
                        }

                    currentList.forEach { upsertHabit(it) }
                }

                is PrepareAnalytics -> {
                    if (action.habit != null) {
                        analytics.trackEvent(
                            AnalyticsEvent.HABIT_ANALYTICS_VIEWED,
                            mapOf("has_reminder" to action.habit.reminder),
                        )
                    }
                    _state.update { it.copy(analyticsHabitId = action.habit?.id) }
                }

                OnAddHabitClicked -> {
                    analytics.trackEvent(AnalyticsEvent.HABIT_SHEET_OPENED, emptyMap())
                    _state.update { it.copy(showHabitAddSheet = true) }
                }

                DismissAddHabitDialog -> {
                    analytics.trackEvent(AnalyticsEvent.HABIT_SHEET_DISMISSED, emptyMap())
                    _state.update { it.copy(showHabitAddSheet = false) }
                }

                is OnToggleCompactView -> datastore.setCompactView(action.pref)

                is OnToggleEditState -> _state.update { it.copy(editState = action.pref) }

                is OnTransientHabitReorder -> {
                    val currentList = _state.value.habitsWithAnalytics.toMutableList()
                    currentList.add(action.to, currentList.removeAt(action.from))
                    _state.update { it.copy(habitsWithAnalytics = currentList) }
                }

                is FetchCompletedHabitsForDate -> {
                    completedHabitsFetchJob?.cancel()
                    completedHabitsFetchJob = launch {
                        if (action.date == null) {
                            _state.update {
                                it.copy(
                                    overallAnalytics =
                                        it.overallAnalytics.copy(completedHabits = null)
                                )
                            }
                            return@launch
                        }

                        val completedHabits = repo.getCompletedHabitsForDate(action.date)

                        _state.update { habitState ->
                            habitState.copy(
                                overallAnalytics =
                                    habitState.overallAnalytics.copy(
                                        completedHabits = action.date to completedHabits
                                    )
                            )
                        }
                    }
                }

                is ChangeFilter -> _state.update { it.copy(selectedFilter = action.filter) }

                is AddHabitCategory -> {
                    if (action.category.id == 0L) {
                        analytics.trackEvent(AnalyticsEvent.HABIT_CATEGORY_CREATED, emptyMap())
                    } else {
                        analytics.trackEvent(AnalyticsEvent.HABIT_CATEGORY_EDITED, emptyMap())
                    }
                    repo.upsertHabitCategory(action.category)
                }

                is DeleteHabitCategory -> {
                    analytics.trackEvent(AnalyticsEvent.HABIT_CATEGORY_DELETED, emptyMap())

                    _state.value.habitsWithAnalytics
                        .filter { it.habit.categoryId == action.category.id }
                        .forEach { scheduler.cancel(it.habit) }

                    repo.deleteHabitCategory(action.category)

                    if (_state.value.selectedFilter == HabitFilter.Category(action.category.id)) {
                        _state.update { it.copy(selectedFilter = HabitFilter.All) }
                    }

                    if (_state.value.analyticsCategoryId == action.category.id) {
                        selectedAnalyticsCategory.value = null
                        _state.update { it.copy(analyticsCategoryId = null) }
                    }
                }

                is ReorderHabitCategories -> {
                    action.mapping.forEach { (index, category) ->
                        repo.upsertHabitCategory(category.copy(index = index))
                    }
                }

                is ChangeAnalyticsCategory -> {
                    selectedAnalyticsCategory.value = action.categoryId
                    _state.update { it.copy(analyticsCategoryId = action.categoryId) }
                }

                OnHabitCategorySheetOpened -> {
                    analytics.trackEvent(AnalyticsEvent.HABIT_CATEGORY_SHEET_OPENED, emptyMap())
                }

                OnHabitCategorySheetDismissed -> {
                    analytics.trackEvent(AnalyticsEvent.HABIT_CATEGORY_SHEET_DISMISSED, emptyMap())
                }

                OnHabitsOpened -> {
                    analytics.trackEvent(AnalyticsEvent.HABITS_OPENED, emptyMap())
                }

                OnOverallAnalyticsViewed -> {
                    analytics.trackEvent(AnalyticsEvent.OVERALL_ANALYTICS_VIEWED, emptyMap())
                }

                is HabitsAction.InsertMeasuredStatus -> {
                    insertHabitStatus(action.habit, action.date, action.value)
                }
            }
        }
    }

    private fun observeHabitStatuses() {
        habitStatusJob?.cancel()
        habitStatusJob =
            viewModelScope.launch {
                combine(repo.getHabitsWithAnalytics(), repo.getCompletedHabitIds()) {
                        habits,
                        completedHabits ->
                        _state.update {
                            it.copy(
                                habitsWithAnalytics = habits,
                                completedHabitIds = completedHabits,
                            )
                        }
                    }
                    .launchIn(this)
            }
    }

    private fun observeHabitCategories() {
        habitCategoryJob?.cancel()
        habitCategoryJob =
            repo
                .getHabitCategoriesFlow()
                .onEach { categories -> _state.update { it.copy(habitCategories = categories) } }
                .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeOverallAnalytics() {
        overallAnalyticsJob?.cancel()
        overallAnalyticsJob =
            selectedAnalyticsCategory
                .flatMapLatest { repo.getOverallAnalytics(it) }
                .onEach { overallAnalytics ->
                    _state.update { it.copy(overallAnalytics = overallAnalytics) }
                }
                .launchIn(viewModelScope)
    }

    private fun observeDataStore() {
        observeDatastoreJob?.cancel()
        observeDatastoreJob =
            viewModelScope.launch {
                datastore
                    .getCompactViewPref()
                    .onEach { pref -> _state.update { it.copy(compactHabitView = pref) } }
                    .launchIn(this)

                datastore
                    .getStartOfTheWeekPref()
                    .onEach { pref -> _state.update { it.copy(startingDay = pref) } }
                    .launchIn(this)

                datastore
                    .getIs24Hr()
                    .onEach { pref -> _state.update { it.copy(is24Hr = pref) } }
                    .launchIn(this)
            }
    }

    private suspend fun rescheduleAllHabits() {
        repo.getHabits().forEach { habit -> scheduler.schedule(habit) }
    }

    private suspend fun upsertHabit(habit: Habit) {
        val newId = repo.upsertHabit(habit)
        scheduler.schedule(habit.copy(id = newId))
    }

    private suspend fun deleteHabit(habit: Habit) {
        repo.deleteHabit(habit.id)
        scheduler.cancel(habit)
    }

    private suspend fun insertHabitStatus(habit: Habit, date: LocalDate) {
        val isHabitCompleted =
            _state.value.habitsWithAnalytics
                .find { it.habit == habit }
                ?.statuses
                ?.any { it.date == date } ?: false

        if (isHabitCompleted) {
            repo.deleteHabitStatus(habit.id, date)
        } else {
            analytics.trackEvent(AnalyticsEvent.HABIT_COMPLETED, emptyMap())
            statusHandler.complete(habit, date)
        }
    }

    private suspend fun insertHabitStatus(habit: Habit, date: LocalDate, value: Double) {
        if (value <= 0.0) {
            repo.deleteHabitStatus(habit.id, date)
            return
        }

        analytics.trackEvent(AnalyticsEvent.HABIT_COMPLETED, emptyMap())
        statusHandler.log(habit, date, value)
    }
}
