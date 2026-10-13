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

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import org.koin.core.annotation.Single

@Single
actual class HabitDbFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<HabitDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(HabitDatabase.DB_NAME)

        return Room.databaseBuilder<HabitDatabase>(appContext, dbFile.absolutePath)
            .addMigrations(HabitDatabase.migrate_3_4, HabitDatabase.migrate_5_6)
    }
}
