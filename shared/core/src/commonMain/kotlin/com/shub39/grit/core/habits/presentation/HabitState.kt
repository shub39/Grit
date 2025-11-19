package com.shub39.grit.core.habits.presentation

import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class HabitState(
    val habitsWithAnalytics: List<HabitWithAnalytics> = emptyList(),
    val completedHabitIds: List<Long> = emptyList(),
    val overallAnalytics: OverallAnalytics = OverallAnalytics(),
    val analyticsHabitId: Long? = null,
    val showHabitAddSheet: Boolean = false,
    val isUserSubscribed: Boolean = false,
    val editState: Boolean = false,

    // datastore
    val compactHabitView: Boolean = false,
    val is24Hr: Boolean = false,
    val startingDay: DayOfWeek = DayOfWeek.MONDAY,
)