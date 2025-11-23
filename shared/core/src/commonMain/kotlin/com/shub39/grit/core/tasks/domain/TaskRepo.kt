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