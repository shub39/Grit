package com.shub39.grit.core.habits.presentation

import com.shub39.grit.core.habits.domain.Habit
import kotlinx.datetime.LocalDate

/**
 * Represents the set of user actions or events that can occur on the Habits screen.
 * This sealed interface is used to pass events from the UI to the ViewModel for processing.
 */
sealed interface HabitsAction {
    /**
     * toggling the compact view mode for the habits list.
     */
    data class OnToggleCompactView(val pref: Boolean) : HabitsAction

    /**
     * toggle habit reordering
     */
    data class OnToggleEditState(val pref: Boolean) : HabitsAction

    /**
     * reorder a single habit
     * [from]: from Index
     * [to]: to Index
     */
    data class OnTransientHabitReorder(val from: Int, val to: Int) : HabitsAction
    data object DismissAddHabitDialog : HabitsAction

    /**
     * open habit upsert sheet
     */
    data object OnAddHabitClicked : HabitsAction

    /**
     * set [habit]'s id to view analytics for
     */
    data class PrepareAnalytics(val habit: Habit?) : HabitsAction
    data class AddHabit(val habit: Habit) : HabitsAction
    data class DeleteHabit(val habit: Habit) : HabitsAction

    /**
     * Add/Remove status for [habit] at given [date]
     */
    data class InsertStatus(
        val habit: Habit,
        val date: LocalDate
    ) : HabitsAction

    data class UpdateHabit(val habit: Habit) : HabitsAction
    data object ReorderHabits : HabitsAction
}