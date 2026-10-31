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

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HabitRepo {
    suspend fun upsertHabit(habit: Habit): Long

    suspend fun deleteHabit(habitId: Long)

    suspend fun getHabits(): List<Habit>

    suspend fun getHabitById(id: Long): Habit?

    suspend fun getHabitStatuses(): List<HabitStatus>

    fun getHabitsWithAnalytics(): Flow<List<HabitWithAnalytics>>

    fun getCompletedHabitIds(): Flow<List<Long>>

    /** Aggregated analytics across all habits, or scoped to [categoryId] when non-null. */
    fun getOverallAnalytics(categoryId: Long? = null): Flow<OverallAnalytics>

    fun getHabitCategoriesFlow(): Flow<List<HabitCategory>>

    suspend fun getHabitCategories(): List<HabitCategory>

    suspend fun upsertHabitCategory(category: HabitCategory)

    /** Deletes the category and all habits belonging to it. */
    suspend fun deleteHabitCategory(category: HabitCategory)

    /** Every habit with its status for today, if it was logged. */
    fun getHabitsWithTodayStatus(): Flow<List<Pair<Habit, HabitStatus?>>>

    suspend fun getStatusForHabit(id: Long): List<HabitStatus>

    suspend fun insertHabitStatus(habitStatus: HabitStatus)

    suspend fun deleteHabitStatus(habitId: Long, date: LocalDate)

    suspend fun getCompletedHabitsForDate(date: LocalDate): Map<Habit, Double?>
}
