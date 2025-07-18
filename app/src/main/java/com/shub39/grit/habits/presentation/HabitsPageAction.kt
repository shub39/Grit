package com.shub39.grit.habits.presentation

import com.shub39.grit.habits.domain.Habit
import java.time.LocalDate

sealed interface HabitsPageAction {
    data object DismissAddHabitDialog : HabitsPageAction
    data object OnAddHabitClicked : HabitsPageAction
    data class PrepareAnalytics(val habit: Habit) : HabitsPageAction
    data class AddHabit(val habit: Habit) : HabitsPageAction
    data class DeleteHabit(val habit: Habit) : HabitsPageAction
    data class InsertStatus(val habit: Habit, val date: LocalDate = LocalDate.now()) :
        HabitsPageAction
    data class UpdateHabit(val habit: Habit) : HabitsPageAction
    data class ReorderHabits(val pairs: List<Pair<Int, Habit>>) : HabitsPageAction
}