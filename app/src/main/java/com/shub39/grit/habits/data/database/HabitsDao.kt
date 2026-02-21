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
package com.shub39.grit.habits.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitsDao {
    @Query("SELECT * FROM habit_index WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?

    @Query("SELECT * FROM habit_index") suspend fun getAllHabits(): List<HabitEntity>

    @Query("SELECT * FROM habit_index") fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Upsert suspend fun upsertHabit(habitEntity: HabitEntity)

    @Query("DELETE FROM habit_index WHERE id = :habitId") suspend fun deleteHabit(habitId: Long)

    @Query("DELETE FROM habit_index") suspend fun deleteAllHabits()
}
