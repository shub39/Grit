package com.shub39.grit.habits.presentation

import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus

data class HabitPageState(
    val habits: List<Habit> = emptyList(),
    val habitsWithStatuses: Map<Long, List<HabitStatus>> = emptyMap(),
    val completedHabits: List<Habit> = emptyList()
)
