package com.shub39.grit.tasks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase?= null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE task RENAME TO task_temp")
                db.execSQL("CREATE TABLE task (id TEXT NOT NULL PRIMARY KEY, title TEXT NOT NULL, priority INTEGER NOT NULL, status INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("INSERT INTO task (id, title, priority, status) SELECT id, title, priority, status FROM task_temp")
                db.execSQL("DROP TABLE task_temp")
            }
        }

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}