package com.shub39.grit.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.habits.domain.HabitStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val scheduler: AlarmScheduler,
    private val repo: HabitRepo
) : ViewModel() {

    private var habitStatusJob: Job? = null

    private val _habitState = MutableStateFlow(HabitPageState())

    val habitsPageState = _habitState.asStateFlow()
        .onStart {
            observeHabitStatuses()
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
                    addHabit(action.habit)
                }

                is HabitsPageAction.DeleteHabit -> {
                    deleteHabit(action.habit)
                }

                is HabitsPageAction.InsertStatus -> {
                    insertHabitStatus(action.habit, action.date)
                }

                is HabitsPageAction.UpdateHabit -> {
                    addHabit(action.habit)
                }

                is HabitsPageAction.ReorderHabits -> {
                    for (habit in action.pairs) {
                        addHabit(habit.second.copy(index = habit.first))
                    }
                }
            }
        }
    }

    private fun observeHabitStatuses() {
        habitStatusJob?.cancel()
        habitStatusJob = repo
            .getHabitStatus()
            .onEach { habitWithStatuses ->
                _habitState.update { habitPageState ->
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

    private suspend fun addHabit(habit: Habit) {
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
        return _habitState.value.habitsWithStatuses[habit]?.any { it.date == date } == true
    }

}