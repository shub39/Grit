package com.shub39.grit.tasks.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shub39.grit.core.data.Converters

@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = TaskDatabase.SCHEMA_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 4, to = 5)
    ]
)
@TypeConverters(Converters::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DB_NAME = "task_database"
        const val SCHEMA_VERSION =  5
    }
}