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
package com.shub39.grit.core.habits

import com.shub39.grit.core.now
import kotlinx.datetime.LocalDate

/**
 * Single write path for habit statuses, shared by the view models, the notification receiver and
 * the home screen widgets.
 */
class HabitStatusHandler(private val repo: HabitRepo) {

    /**
     * Adds a status for [date], or removes it when the day is already logged. Measurable habits are
     * logged with their full target value.
     */
    suspend fun toggle(habit: Habit, date: LocalDate = LocalDate.now()) {
        val isLogged = repo.getStatusForHabit(habit.id).any { it.date == date }

        if (isLogged) repo.deleteHabitStatus(habit.id, date) else complete(habit, date)
    }

    /** Logs [date] as completed, using [Habit.targetValue] for measurable habits. */
    suspend fun complete(habit: Habit, date: LocalDate = LocalDate.now()) {
        repo.insertHabitStatus(
            HabitStatus(
                habitId = habit.id,
                date = date,
                value = if (habit.isMeasurable) habit.targetValue else 1.0,
            )
        )
    }

    /** Sets the logged value for [date], removing the status when [value] is not positive. */
    suspend fun log(habit: Habit, date: LocalDate = LocalDate.now(), value: Double) {
        if (value <= 0.0) {
            repo.deleteHabitStatus(habit.id, date)
        } else {
            repo.insertHabitStatus(HabitStatus(habitId = habit.id, date = date, value = value))
        }
    }
}
