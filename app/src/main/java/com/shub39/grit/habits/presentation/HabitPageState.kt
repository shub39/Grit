package com.shub39.grit.habits.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.habits.domain.HabitWithAnalytics
import com.shub39.grit.habits.domain.OverallAnalytics
import kotlinx.datetime.DayOfWeek

@Stable
@Immutable
data class HabitPageState(
    val habitsWithAnalytics: List<HabitWithAnalytics> = emptyList(),
    val completedHabitIds: List<Long> = emptyList(),
    val overallAnalytics: OverallAnalytics = OverallAnalytics(),
    val analyticsHabitId: Long? = null,
    val showHabitAddSheet: Boolean = false,
    val isUserSubscribed: Boolean = false,

    // datastore
    val compactHabitView: Boolean = false,
    val is24Hr: Boolean = false,
    val timeFormat: String = "hh:mm a",
    val startingDay: DayOfWeek = DayOfWeek.MONDAY,
)