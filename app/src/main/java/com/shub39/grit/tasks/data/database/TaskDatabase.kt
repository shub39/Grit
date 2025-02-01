package com.shub39.grit.tasks.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 4, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DB_NAME = "task_database"
    }
}