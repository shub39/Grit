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
    autoMigrations = [AutoMigration(from = 4, to = 5)],
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TasksDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DB_NAME = "task_database"
        const val SCHEMA_VERSION = 5
    }
}
