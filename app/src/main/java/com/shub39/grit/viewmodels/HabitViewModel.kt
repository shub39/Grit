package com.shub39.grit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    private val stateLayer: StateLayer,
    private val billingHandler: BillingHandler,
    private val scheduler: AlarmScheduler,
    private val repo: HabitRepo,
    private val datastore: GritDatastore,
) : ViewModel() {

    private var habitStatusJob: Job? = null
    private var overallAnalyticsJob: Job? = null
    private var observeJob: Job? = null

    private val _state = stateLayer.habitsState

    val state = _state.asStateFlow()
        .onStart {
            observeDataStore()
            observeHabitStatuses()
            observeOverallAnalytics()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HabitPageState()
        )

    // handles actions from habit page
    fun habitsPageAction(action: HabitsPageAction) {
        viewModelScope.launch {
            when (action) {
                is HabitsPageAction.AddHabit -> {
                    upsertHabit(action.habit)
                }

                is HabitsPageAction.DeleteHabit -> {
                    deleteHabit(action.habit)
                }

                is HabitsPageAction.InsertStatus -> {
                    insertHabitStatus(action.habit, action.date)
                }

                is HabitsPageAction.UpdateHabit -> {
                    upsertHabit(action.habit)
                }

                is HabitsPageAction.ReorderHabits -> {
                    for (habitWithIndex in action.pairs) {
                        upsertHabit(habitWithIndex.second.habit.copy(index = habitWithIndex.first))
                    }
                }

                is HabitsPageAction.PrepareAnalytics -> {
                    _state.update {
                        it.copy(
                            analyticsHabitId = action.habit.id
                        )
                    }
                }

                HabitsPageAction.OnAddHabitClicked -> {
                    val isSubscribed = billingHandler.isPlusUser()

                    if (!isSubscribed && _state.value.habitsWithAnalytics.size >= 5) {
                        stateLayer.settingsState.update {
                            it.copy(
                                showPaywall = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                showHabitAddSheet = true
                            )
                        }

                        if (isSubscribed) {
                            stateLayer.settingsState.update {
                                it.copy(
                                    isUserSubscribed = true
                                )
                            }

                            _state.update {
                                it.copy(isUserSubscribed = true)
                            }
                        }
                    }
                }

                HabitsPageAction.DismissAddHabitDialog -> _state.update { it.copy(showHabitAddSheet = false) }

                HabitsPageAction.OnShowPaywall -> stateLayer.settingsState.update {
                    it.copy(
                        showPaywall = true
                    )
                }

                is HabitsPageAction.OnToggleCompactView -> datastore.setCompactView(action.pref)
            }
        }
    }

    private fun observeHabitStatuses() {
        habitStatusJob?.cancel()
        habitStatusJob = repo
            .getHabitStatus(_state.value.startingDay)
            .onEach { habitsWithAnalytics ->
                _state.update { habitPageState ->
                    habitPageState.copy(
                        habitsWithAnalytics = habitsWithAnalytics,
                        completedHabitIds = habitsWithAnalytics
                            .filter { habitWithAnalytics ->
                                habitWithAnalytics.statuses.any { it.date == LocalDate.now() }
                            }
                            .map { it.habit.id }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeOverallAnalytics() {
        overallAnalyticsJob?.cancel()
        overallAnalyticsJob = repo
            .getOverallAnalytics(_state.value.startingDay)
            .onEach { overallAnalytics ->
                _state.update {
                    it.copy(
                        overallAnalytics = overallAnalytics
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeDataStore() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            datastore
                .getCompactViewPref()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            compactHabitView = pref
                        )
                    }
                }
                .launchIn(this)

            datastore
                .getStartOfTheWeekPref()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            startingDay = pref
                        )
                    }
                }
                .launchIn(this)

            datastore
                .getIs24Hr()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            is24Hr = pref,
                            timeFormat = if (pref) "HH:mm" else "hh:mm a"
                        )
                    }
                }
                .launchIn(this)
        }
    }

    private suspend fun upsertHabit(habit: Habit) {
        repo.upsertHabit(habit)
        scheduler.schedule(habit)
    }

    private suspend fun deleteHabit(habit: Habit) {
        repo.deleteHabit(habit.id)
        scheduler.cancel(habit)
    }

    private suspend fun insertHabitStatus(habit: Habit, date: LocalDate) {
        val isHabitCompleted = _state.value.habitsWithAnalytics.find { it.habit == habit }?.statuses?.any { it.date == date } ?: false

        if (isHabitCompleted) {

            repo.deleteHabitStatus(habit.id, date)

        } else {
            repo.insertHabitStatus(
                HabitStatus(
                    habitId = habit.id,
                    date = date
                )
            )
        }
    }

}