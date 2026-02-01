package com.shub39.grit.core.habits.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import kotlinx.datetime.DayOfWeek


/**
 * Represents the overall state for the habits feature.
 *
 * @property habitsWithAnalytics A list of all habits, each bundled with its analytical data.
 * @property completedHabitIds A list of IDs for habits that have been marked as completed for the current day.
 * @property overallAnalytics Aggregated analytics data across all habits.
 * @property analyticsHabitId The ID of the habit currently selected for detailed analytics view, or null if none is selected.
 * @property showHabitAddSheet A boolean flag to control the visibility of the "add new habit" bottom sheet.
 * @property editState A boolean flag indicating if the UI is in an "edit" mode, allowing for reordering or deletion of habits.
 * @property compactHabitView A user preference flag to display habits in a compact or detailed view.
 * @property is24Hr A user preference flag to display time in 24-hour format.
 * @property startingDay A user preference for the day of the week the calendar/week view should start on.
 */
@Stable
@Immutable
data class HabitState(
    val habitsWithAnalytics: List<HabitWithAnalytics> = emptyList(),
    val completedHabitIds: List<Long> = emptyList(),
    val overallAnalytics: OverallAnalytics = OverallAnalytics(),
    val analyticsHabitId: Long? = null,
    val showHabitAddSheet: Boolean = false,
    val editState: Boolean = false,

    // datastore
    val compactHabitView: Boolean = false,
    val is24Hr: Boolean = false,
    val startingDay: DayOfWeek = DayOfWeek.MONDAY,
)