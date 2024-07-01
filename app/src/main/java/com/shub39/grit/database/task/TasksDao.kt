package com.shub39.grit.database.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TasksDao {
    @Query("SELECT * FROM task")
    suspend fun getTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM TASK")
    suspend fun deleteAllTasks()

    @Update
    suspend fun updateTask(task: Task)
}