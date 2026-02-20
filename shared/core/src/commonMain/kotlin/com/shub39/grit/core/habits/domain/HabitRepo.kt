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
package com.shub39.grit.core.habits.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HabitRepo {
    suspend fun upsertHabit(habit: Habit)

    suspend fun deleteHabit(habitId: Long)

    suspend fun getHabits(): List<Habit>

    suspend fun getHabitById(id: Long): Habit?

    suspend fun getHabitStatuses(): List<HabitStatus>

    fun getHabitsWithAnalytics(): Flow<List<HabitWithAnalytics>>

    fun getCompletedHabitIds(): Flow<List<Long>>

    fun getOverallAnalytics(): Flow<OverallAnalytics>

    fun getHabitsWithStatus(): Flow<List<Pair<Habit, Boolean>>>

    suspend fun getStatusForHabit(id: Long): List<HabitStatus>

    suspend fun insertHabitStatus(habitStatus: HabitStatus)

    suspend fun deleteHabitStatus(habitId: Long, date: LocalDate)
}
