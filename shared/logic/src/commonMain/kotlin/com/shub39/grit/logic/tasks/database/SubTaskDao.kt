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
package com.shub39.grit.logic.tasks.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {
    @Query("SELECT * FROM sub_tasks") fun getSubTasksFlow(): Flow<List<SubTaskEntity>>

    @Query("SELECT * FROM sub_tasks") suspend fun getSubTasks(): List<SubTaskEntity>

    @Query("SELECT * FROM sub_tasks WHERE id = :id")
    suspend fun getSubTaskById(id: Long): SubTaskEntity?

    @Upsert suspend fun upsertSubTask(subTaskEntity: SubTaskEntity): Long

    @Delete suspend fun deleteSubTask(subTaskEntity: SubTaskEntity)
}
