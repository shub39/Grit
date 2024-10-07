package com.shub39.grit.viewModel

import android.app.Application
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

class HabitViewModel(application: Application) : ViewModel() {

    private val habitDatabase = HabitDatabase.getDatabase(application)
    private val habitDao = habitDatabase.habitDao()
    private val habitStatusDao = habitDatabase.habitStatusDao()

    private val _habits = MutableStateFlow(listOf<Habit>())
    private val _habitsWithStatuses = MutableStateFlow(mapOf<String, List<HabitStatus>>())
    private val scheduler = NotificationAlarmScheduler(application.applicationContext)

    val habits: StateFlow<List<Habit>> get() = _habits

    init {
        viewModelScope.launch {
            _habits.value = habitDao.getAllHabits()
            updateStatusMap()
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value += habit
            habitDao.insertHabit(habit)
            scheduler.schedule(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value -= habit
            habitDao.deleteHabit(habit)
            scheduler.cancel(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value = habitDao.getAllHabits()
            habitDao.updateHabit(habit)
            scheduler.schedule(habit)
        }
    }

    fun insertHabitStatus(habitId: String, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            habitStatusDao.insertHabitStatus(
                HabitStatus(
                    habitId = habitId,
                    date = date
                )
            )
            updateStatusMap(habitId)
        }
    }

    fun deleteHabitStatus(habitId: String, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            habitStatusDao.deleteStatus(habitId, date)
            updateStatusMap(habitId)
        }
    }

    fun getHabitStatus(habitId: String): List<HabitStatus> {
        return _habitsWithStatuses.value[habitId] ?: emptyList()
    }

    fun isHabitCompleted(habitId: String): Boolean {
        val result = getHabitStatus(habitId).find {
            it.habitId == habitId && it.date == LocalDate.now()
        } != null
        Log.d("HabitViewModel", "isHabitCompleted: $result")
        return result
    }

    private suspend fun updateStatusMap() {
        _habits.value.forEach {
            val statusList = habitStatusDao.getStatusForHabit(it.id)
            _habitsWithStatuses.value += it.id to statusList
        }
    }

    private suspend fun updateStatusMap(habitId: String) {
        _habitsWithStatuses.value += habitId to habitStatusDao.getStatusForHabit(habitId)
    }

}

