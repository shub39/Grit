/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.habits.presentation

import com.shub39.grit.core.habits.domain.Habit
import kotlinx.datetime.LocalDate

/**
 * Represents the set of user actions or events that can occur on the Habits screen. This sealed
 * interface is used to pass events from the UI to the ViewModel for processing.
 */
sealed interface HabitsAction {
    /** toggling the compact view mode for the habits list. */
    data class OnToggleCompactView(val pref: Boolean) : HabitsAction

    /** toggle habit reordering */
    data class OnToggleEditState(val pref: Boolean) : HabitsAction

    /** reorder a single habit [from]: from Index [to]: to Index */
    data class OnTransientHabitReorder(val from: Int, val to: Int) : HabitsAction

    data object DismissAddHabitDialog : HabitsAction

    /** open habit upsert sheet */
    data object OnAddHabitClicked : HabitsAction

    /** set [habit]'s id to view analytics for */
    data class PrepareAnalytics(val habit: Habit?) : HabitsAction

    data class AddHabit(val habit: Habit) : HabitsAction

    data class DeleteHabit(val habit: Habit) : HabitsAction

    /** Add/Remove status for [habit] at given [date] */
    data class InsertStatus(val habit: Habit, val date: LocalDate) : HabitsAction

    data class UpdateHabit(val habit: Habit) : HabitsAction

    data object ReorderHabits : HabitsAction
}
