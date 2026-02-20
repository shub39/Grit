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
package com.shub39.grit.tasks.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Query("SELECT * FROM task") fun getTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task") suspend fun getTasks(): List<TaskEntity>

    @Query("UPDATE task SET `index` = :newIndex WHERE id = :id")
    suspend fun updateTaskIndexById(id: Long, newIndex: Int)

    @Query("SELECT * FROM task WHERE id = :id") suspend fun getTaskById(id: Long): TaskEntity?

    @Upsert suspend fun upsertTask(taskEntity: TaskEntity)

    @Delete suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM task") suspend fun deleteAllTasks()
}
