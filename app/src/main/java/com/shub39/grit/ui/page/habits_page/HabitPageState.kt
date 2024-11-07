package com.shub39.grit.ui.page.habits_page

import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.HabitStatus

data class HabitPageState(
    val habits: List<Habit> = emptyList(),
    val habitsWithStatuses: Map<Long, List<HabitStatus>> = emptyMap(),
    val completedHabits: List<Habit> = emptyList()
)
