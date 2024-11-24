package com.shub39.grit.tasks.domain

import kotlinx.coroutines.flow.Flow

interface TaskRepo {
    fun getTasks(): Flow<List<Task>>
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(task: Task)
}