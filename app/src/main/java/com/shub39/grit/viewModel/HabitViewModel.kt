package com.shub39.grit.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.shub39.grit.database.habit.DailyHabitStatus
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel(applicationContext: Context) : ViewModel() {
    private val habitDatabase = Room.databaseBuilder(
        applicationContext,
        HabitDatabase::class.java,
        "habit-database"
    ).build()
    private val habitDao = habitDatabase.habitDao()
    private val _habits = MutableStateFlow(listOf<Habit>())

    val habits: StateFlow<List<Habit>> get() = _habits

    init {
        viewModelScope.launch {
            _habits.value = habitDao.getAllHabits()
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            _habits.value += habit
            habitDao.insertHabit(habit)
        }
    }

    fun addStatusForHabit(habit: DailyHabitStatus) {
        viewModelScope.launch {
            habitDao.insertDailyHabitStatus(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.deleteHabit(habit)
            habitDao.deleteDailyStatusForHabit(habit.id)
        }
    }

}