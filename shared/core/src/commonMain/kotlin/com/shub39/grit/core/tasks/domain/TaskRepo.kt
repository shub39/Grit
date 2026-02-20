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
package com.shub39.grit.core.tasks.domain

import kotlinx.coroutines.flow.Flow

interface TaskRepo {
    fun getTasksFlow(): Flow<Map<Category, List<Task>>>

    fun getCompletedTasksFlow(): Flow<List<Task>>

    suspend fun getTasks(): List<Task>

    suspend fun getTaskById(id: Long): Task?

    suspend fun getCategories(): List<Category>

    suspend fun updateTaskIndexById(id: Long, index: Int)

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteAllTasks()

    suspend fun upsertCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun deleteAllCategories()
}
