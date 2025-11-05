package com.shub39.grit.habits.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import java.time.DayOfWeek

@Stable
@Immutable
data class HabitPageState(
    val habitsWithStatuses: Map<Habit, List<HabitStatus>> = emptyMap(),
    val completedHabits: List<Habit> = emptyList(),
    val analyticsHabitId: Long? = null,
    val showHabitAddSheet: Boolean = false,
    val isUserSubscribed: Boolean = false,

    // datastore
    val compactHabitView: Boolean = false,
    val is24Hr: Boolean = false,
    val timeFormat: String = "hh:mm a",
    val startingDay: DayOfWeek = DayOfWeek.MONDAY,
)