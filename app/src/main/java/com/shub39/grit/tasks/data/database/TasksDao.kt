package com.shub39.grit.tasks.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Query("SELECT * FROM task")
    fun getTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task")
    fun getTasks(): List<TaskEntity>

    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()
}