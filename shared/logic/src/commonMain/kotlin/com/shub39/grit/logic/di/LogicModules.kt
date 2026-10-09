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
package com.shub39.grit.logic.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.shub39.grit.logic.core.datastore.DatastoreFactory
import com.shub39.grit.logic.habits.database.HabitDatabase
import com.shub39.grit.logic.habits.database.HabitDbFactory
import com.shub39.grit.logic.habits.database.HabitStatusDao
import com.shub39.grit.logic.habits.database.HabitsDao
import com.shub39.grit.logic.tasks.database.CategoryDao
import com.shub39.grit.logic.tasks.database.SubTaskDao
import com.shub39.grit.logic.tasks.database.TaskDatabase
import com.shub39.grit.logic.tasks.database.TaskDbFactory
import com.shub39.grit.logic.tasks.database.TasksDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.shub39.grit.logic")
class LogicModules {
    @Single
    fun getDatastorePreferences(factory: DatastoreFactory): DataStore<Preferences> =
        factory.getPreferencesDataStore()

    @Single fun getTaskDatabase(factory: TaskDbFactory): TaskDatabase = factory.create().build()

    @Single fun getHabitDatabase(factory: HabitDbFactory): HabitDatabase = factory.create().build()

    @Single fun getTasksDao(db: TaskDatabase): TasksDao = db.taskDao()

    @Single fun getCategoryDao(db: TaskDatabase): CategoryDao = db.categoryDao()

    @Single fun getSubTasksDao(db: TaskDatabase): SubTaskDao = db.subTaskDao()

    @Single fun getHabitsDao(db: HabitDatabase): HabitsDao = db.habitDao()

    @Single fun getHabitStatusDao(db: HabitDatabase): HabitStatusDao = db.habitStatusDao()
}
