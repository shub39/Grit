package com.shub39.grit.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitDatabase
import com.shub39.grit.database.habit.HabitStatus
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    habitDatabase: HabitDatabase,
    scheduler: NotificationAlarmScheduler
) : ViewModel() {

    private val habitDao = habitDatabase.habitDao()
    private val habitStatusDao = habitDatabase.habitStatusDao()

    private val _habits = MutableStateFlow(listOf<Habit>())
    private val _habitsWithStatuses = MutableStateFlow(mapOf<String, List<HabitStatus>>())
    private val _scheduler = scheduler

    val habits: StateFlow<List<Habit>> get() = _habits

    init {
        viewModelScope.launch {
            _habits.value = habitDao.getAllHabits()
            updateStatusMapForAllHabits()
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value += habit
            habitDao.insertHabit(habit)
            _scheduler.schedule(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value -= habit
            habitDao.deleteHabit(habit)
            _scheduler.cancel(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.updateHabit(habit)
            _habits.value = habitDao.getAllHabits()
            _scheduler.schedule(habit)
        }
    }

    fun insertHabitStatus(habit: Habit, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            if (!isHabitCompleted(habit, date)) {
                habitStatusDao.insertHabitStatus(
                    HabitStatus(
                        habitId = habit.id,
                        date = date
                    )
                )
            } else {
                habitStatusDao.deleteStatus(habit.id, date)
            }
            updateStatusMap(habit)
        }
    }

    fun getHabitStatus(habit: Habit): List<HabitStatus> {
        return _habitsWithStatuses.value[habit.id] ?: emptyList()
    }

    fun isHabitCompleted(habit: Habit, date: LocalDate = LocalDate.now()): Boolean {
        val isCompleted = getHabitStatus(habit).any {
            it.date == date
        }
        Log.d("HabitViewModel", "isHabitCompleted: $isCompleted for habitId: $habit")
        return isCompleted
    }

    private suspend fun updateStatusMapForAllHabits() {
        val newStatusMap = _habits.value.associate { habit ->
            habit.id to habitStatusDao.getStatusForHabit(habit.id)
        }
        _habitsWithStatuses.value = newStatusMap
    }

    private suspend fun updateStatusMap(habit: Habit) {
        val updatedStatus = habitStatusDao.getStatusForHabit(habit.id)
        _habitsWithStatuses.value = _habitsWithStatuses.value.toMutableMap().apply {
            this[habit.id] = updatedStatus
        }
    }

}