package com.shub39.grit.habits.presentation

import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus

data class HabitPageState(
    val habitsWithStatuses: Map<Habit, List<HabitStatus>> = emptyMap(),
    val completedHabits: List<Habit> = emptyList()
)
