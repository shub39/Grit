package com.shub39.grit.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.habit.DailyHabitStatus
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitDatabase
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
            habits.value.forEach {
                scheduler.cancel(it)
                scheduler.schedule(it)
            }
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value += habit
            habitDao.insertHabit(habit)
            scheduler.schedule(habit)
        }
    }

    fun addStatusForHabit(string: String) {
        viewModelScope.launch {
            val dailyHabitStatus = DailyHabitStatus(LocalDateTime.now(), string, LocalDate.now())
            _habitStatuses.value += dailyHabitStatus
            habitStatusDao.insertDailyStatus(dailyHabitStatus)
            Log.d("TAG", "addStatusForHabit: $dailyHabitStatus")
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
            scheduler.cancel(habit)
            scheduler.schedule(habit)
        }
    }

    private fun countConsecutiveDaysBeforeLast(dates: List<LocalDate>): Int {
        if (dates.size < 2) return 0
        val sortedDates = dates.sorted()
        var consecutiveCount = 0
        for (i in sortedDates.size - 2 downTo 0) {
            val currentDate = sortedDates[i]
            val nextDate = sortedDates[i + 1]
            if (ChronoUnit.DAYS.between(currentDate, nextDate) == 1L) {
                consecutiveCount++
            } else {
                break
            }
        }
        return consecutiveCount
    }

    private fun countBestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val sortedDates = dates.sorted()
        var maxConsecutive = 0
        var currentConsecutive = 0
        for (i in 1 until sortedDates.size) {
            val previousDate = sortedDates[i - 1]
            val currentDate = sortedDates[i]
            if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
                currentConsecutive++
            } else {
                if (currentConsecutive > maxConsecutive) {
                    maxConsecutive = currentConsecutive
                }
                currentConsecutive = 1
            }
        }
        if (currentConsecutive > maxConsecutive) {
            maxConsecutive = currentConsecutive
        }
        return maxConsecutive
    }

}

