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
    suspend fun getTasks(): List<TaskEntity>

    @Query("UPDATE task SET `index` = :newIndex WHERE id = :id")
    suspend fun updateTaskIndexById(id: Long, newIndex: Int)

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()
}