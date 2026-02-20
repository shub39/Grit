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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface HabitStatusDao {
    @Query("SELECT * FROM habit_status") suspend fun getHabitStatuses(): List<HabitStatusEntity>

    @Query("SELECT * FROM habit_status WHERE date = :date")
    suspend fun getCompletedStatuses(date: LocalDate): List<HabitStatusEntity>

    @Query("SELECT * FROM habit_status") fun getAllHabitStatuses(): Flow<List<HabitStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitStatus(habitStatusEntity: HabitStatusEntity)

    @Query("SELECT * FROM habit_status WHERE habitId = :habitId")
    suspend fun getStatusForHabit(habitId: Long): List<HabitStatusEntity>

    @Query("DELETE FROM habit_status WHERE habitId = :habitId AND date = :date")
    suspend fun deleteStatus(habitId: Long, date: LocalDate)

    @Query("DELETE FROM habit_status") suspend fun deleteAllHabitStatus()
}
