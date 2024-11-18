package com.shub39.grit.ui.page.habits_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitDatabase
import com.shub39.grit.database.habit.HabitStatus
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    habitDatabase: HabitDatabase,
    scheduler: NotificationAlarmScheduler
) : ViewModel() {

    private val habitDao = habitDatabase.habitDao()
    private val habitStatusDao = habitDatabase.habitStatusDao()

    private val _scheduler = scheduler
    private val _habitState = MutableStateFlow(HabitPageState())

    val habitsPageState = _habitState.asStateFlow()
        .onStart { onStart() }
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
                    updateHabit(action.habit)
                }
            }
        }
    }

    private suspend fun onStart() {
        val habits = habitDao.getAllHabits()

        _habitState.update {
            it.copy(
                habits = habits,
                habitsWithStatuses = habits.associate { habit ->
                    habit.id to habitStatusDao.getStatusForHabit(habit.id)
                },
                completedHabits = habits.filter { habit ->
                    isHabitCompleted(habit, LocalDate.now())
                }
            )
        }
    }

    private suspend fun addHabit(habit: Habit) {
        habitDao.insertHabit(habit)
        _habitState.update {
            it.copy(
                habits = it.habits.plus(habit),
            )
        }
        _scheduler.schedule(habit)
    }

    private suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
        _habitState.update {
            it.copy(
                habits = it.habits.minus(habit),
                habitsWithStatuses = it.habitsWithStatuses.minus(habit.id),
                completedHabits = it.completedHabits.filter { completedHabit ->
                    completedHabit.id != habit.id
                }
            )
        }
        _scheduler.cancel(habit)
    }

    private suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
        _habitState.update {
            it.copy(
                habits = habitDao.getAllHabits()
            )
        }
        _scheduler.schedule(habit)
    }

    private suspend fun insertHabitStatus(habit: Habit, date: LocalDate) {
        if (isHabitCompleted(habit, date)) {
            habitStatusDao.deleteStatus(habit.id, date)

            _habitState.update {
                it.copy(
                    completedHabits = it.completedHabits.minus(habit)
                )
            }
        } else {
            habitStatusDao.insertHabitStatus(
                HabitStatus(
                    habitId = habit.id,
                    date = date
                )
            )

            _habitState.update {
                it.copy(
                    completedHabits = it.completedHabits.plus(habit)
                )
            }
        }

        _habitState.update {
            it.copy(
                habitsWithStatuses = it.habitsWithStatuses.toMutableMap().apply {
                    this[habit.id] = habitStatusDao.getStatusForHabit(habit.id)
                }
            )
        }
    }

    private suspend fun isHabitCompleted(habit: Habit, date: LocalDate): Boolean {
        return getHabitStatus(habit).any {
            it.date == date
        }
    }

    private suspend fun getHabitStatus(habit: Habit): List<HabitStatus> {
        return habitStatusDao.getStatusForHabit(habit.id)
    }

}