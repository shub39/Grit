package com.shub39.grit.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.habit.DailyHabitStatus
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitDatabase
import com.shub39.grit.logic.OtherLogic.countBestStreak
import com.shub39.grit.logic.OtherLogic.countConsecutiveDaysBeforeLast
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HabitViewModel(application: Application) : ViewModel() {

    private val habitDatabase = HabitDatabase.getDatabase(application)
    private val habitDao = habitDatabase.habitDao()
    private val habitStatusDao = habitDatabase.dailyHabitStatusDao()
    private val _habits = MutableStateFlow(listOf<Habit>())
    private val _habitStatuses = MutableStateFlow(listOf<DailyHabitStatus>())
    private val scheduler = NotificationAlarmScheduler(application.applicationContext)

    val habits: StateFlow<List<Habit>> get() = _habits
    private val habitStatuses: StateFlow<List<DailyHabitStatus>> get() = _habitStatuses

    init {
        viewModelScope.launch {
            _habits.value = habitDao.getAllHabits()
            _habitStatuses.value = habitStatusDao.getDailyStatusForHabit()
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value += habit
            habitDao.insertHabit(habit)
            scheduler.schedule(habit)
        }
    }

    fun addStatusForHabit(string: String, time: LocalDateTime = LocalDateTime.now()) {
        viewModelScope.launch {
            val dailyHabitStatus = _habitStatuses.value.find { it.id == string && it.date == time.toLocalDate() }
            if (dailyHabitStatus == null) {
                val status = DailyHabitStatus(time, string, time.toLocalDate())
                habitStatusDao.insertDailyStatus(status)
                _habitStatuses.value += status
                Log.d("TAG", "added status for Habit: $string")
            } else {
                habitStatusDao.deleteDailyStatus(dailyHabitStatus)
                _habitStatuses.value -= dailyHabitStatus
                Log.d("TAG", "removed status for Habit: $string")
            }
        }
    }

    fun isStatusAdded(string: String): Boolean {
        var isAdded = listOf<DailyHabitStatus>()
        viewModelScope.launch {
            isAdded = habitStatuses.value.filter { it.id == string && it.date == LocalDate.now() }
        }
        return isAdded.isNotEmpty()
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.deleteHabit(habit)
            _habits.value -= habit
            _habitStatuses.value.forEach {
                if (it.id == habit.id) {
                    _habitStatuses.value -= it
                }
            }
            scheduler.cancel(habit)
        }
    }

    fun getStatusForHabit(string: String): List<DailyHabitStatus> {
        var list = listOf<DailyHabitStatus>()
        viewModelScope.launch {
            list = habitStatuses.value.filter { it.id == string }
        }
        return list
    }

    fun getStreakForHabit(string: String): Int {
        val list = getStatusForHabit(string)
        var streak = 0
        viewModelScope.launch {
            streak = countConsecutiveDaysBeforeLast(list.map { it.date })
        }
        return streak
    }

    fun getBestStreakForHabit(string: String): Int {
        val list = getStatusForHabit(string)
        var streak = 0
        viewModelScope.launch {
            streak = countBestStreak(list.map { it.date })
        }
        return streak
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.updateHabit(habit)
            _habits.value = habitDao.getAllHabits()
            scheduler.schedule(habit)
        }
    }

}

