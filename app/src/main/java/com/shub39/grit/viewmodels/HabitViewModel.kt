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
    private var observeJob: Job? = null

    private val _state = stateLayer.habitsState

    val state = _state.asStateFlow()
        .onStart {
            observeHabitStatuses()
            observeDataStore()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
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
                    for (habit in action.pairs) {
                        upsertHabit(habit.second.copy(index = habit.first))
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

                    if (!isSubscribed && _state.value.habitsWithStatuses.size >= 5) {
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

                HabitsPageAction.OnShowPaywall -> stateLayer.settingsState.update { it.copy(showPaywall = true) }

                is HabitsPageAction.OnToggleCompactView -> datastore.setCompactView(action.pref)
            }
        }
    }

    private fun observeHabitStatuses() {
        habitStatusJob?.cancel()
        habitStatusJob = repo
            .getHabitStatus()
            .onEach { habitWithStatuses ->
                _state.update { habitPageState ->
                    habitPageState.copy(
                        habitsWithStatuses = habitWithStatuses,
                        completedHabits = habitWithStatuses.keys.filter { habit ->
                            habitWithStatuses[habit]?.any { it.date == LocalDate.now() } == true
                        }
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
        if (isHabitCompleted(habit, date)) {

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

    private fun isHabitCompleted(habit: Habit, date: LocalDate): Boolean {
        return _state.value.habitsWithStatuses[habit]?.any { it.date == date } == true
    }

}