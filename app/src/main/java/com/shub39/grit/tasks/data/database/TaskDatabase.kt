package com.shub39.grit.tasks.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 5, exportSchema = false)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DB_NAME = "task_database"
        
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add deadline column (nullable timestamp)
                database.execSQL("ALTER TABLE task ADD COLUMN deadline INTEGER DEFAULT NULL")
                // Add priority column with default value
                database.execSQL("ALTER TABLE task ADD COLUMN priority TEXT NOT NULL DEFAULT 'MEDIUM'")
            }
        }
    }
}