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
package com.shub39.grit.logic.habits.database

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.shub39.grit.logic.core.Converters

@Database(
    entities = [HabitEntity::class, HabitStatusEntity::class, HabitCategoryEntity::class],
    version = HabitDatabase.SCHEMA_VERSION,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 4, to = 5)],
)
@ConstructedBy(HabitsDbConstructor::class)
@ColumnTypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitsDao

    abstract fun habitStatusDao(): HabitStatusDao

    abstract fun habitCategoryDao(): HabitCategoryDao

    companion object {
        const val SCHEMA_VERSION = 7
        const val DB_NAME = "habit_database"

        val migrate_3_4 =
            object : Migration(3, 4) {
                override suspend fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        "ALTER TABLE habit_index ADD COLUMN days TEXT NOT NULL DEFAULT '${Converters.allDays}'"
                    )
                }
            }

        val migrate_5_6 =
            object : Migration(5, 6) {
                override suspend fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        "ALTER TABLE habit_index ADD COLUMN target_value REAL NOT NULL DEFAULT 0.0"
                    )
                    connection.execSQL(
                        "ALTER TABLE habit_index ADD COLUMN target_unit TEXT NOT NULL DEFAULT ''"
                    )
                    connection.execSQL(
                        "ALTER TABLE habit_status ADD COLUMN value REAL NOT NULL DEFAULT 1.0"
                    )
                }
            }

        val migrate_6_7 =
            object : Migration(6, 7) {
                override suspend fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        "CREATE TABLE IF NOT EXISTS `habit_categories` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`index` INTEGER NOT NULL, " +
                            "`color` TEXT NOT NULL)"
                    )
                    connection.execSQL("ALTER TABLE habit_index ADD COLUMN category_id INTEGER")
                    connection.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_habit_index_category_id " +
                            "ON habit_index(category_id)"
                    )
                }
            }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object HabitsDbConstructor : RoomDatabaseConstructor<HabitDatabase> {
    override fun initialize(): HabitDatabase
}
