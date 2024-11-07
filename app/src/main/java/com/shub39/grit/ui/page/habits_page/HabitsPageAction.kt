package com.shub39.grit.ui.page.habits_page

import com.shub39.grit.database.habit.Habit
import java.time.LocalDate

sealed interface HabitsPageAction {
    data class AddHabit(val habit: Habit) : HabitsPageAction
    data class DeleteHabit(val habit: Habit) : HabitsPageAction
    data class InsertStatus(val habit: Habit, val date: LocalDate = LocalDate.now()) : HabitsPageAction
    data class UpdateHabit(val habit: Habit) : HabitsPageAction
}