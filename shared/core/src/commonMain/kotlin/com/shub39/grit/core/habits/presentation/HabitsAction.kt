package com.shub39.grit.core.habits.presentation

import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import kotlinx.datetime.LocalDate

sealed interface HabitsAction {
    data class OnToggleCompactView(val pref: Boolean) : HabitsAction

    data object OnShowPaywall : HabitsAction
    data object DismissAddHabitDialog : HabitsAction
    data object OnAddHabitClicked : HabitsAction
    data class PrepareAnalytics(val habit: Habit) : HabitsAction
    data class AddHabit(val habit: Habit) : HabitsAction
    data class DeleteHabit(val habit: Habit) : HabitsAction
    data class InsertStatus(val habit: Habit, val date: LocalDate) :
        HabitsAction
    data class UpdateHabit(val habit: Habit) : HabitsAction
    data class ReorderHabits(val pairs: List<Pair<Int, HabitWithAnalytics>>) : HabitsAction
}