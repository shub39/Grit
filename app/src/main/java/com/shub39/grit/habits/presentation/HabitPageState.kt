package com.shub39.grit.habits.presentation

import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import java.time.DayOfWeek

data class HabitPageState(
    val habitsWithStatuses: Map<Habit, List<HabitStatus>> = emptyMap(),
    val completedHabits: List<Habit> = emptyList(),
    val is24Hr: Boolean = false,
    val timeFormat: String = "hh:mm a",
    val startingDay: DayOfWeek = DayOfWeek.MONDAY,
    val analyticsHabitId: Long? = null
)