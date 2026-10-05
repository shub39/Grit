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
package com.shub39.grit.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.shub39.grit.analytics.AnalyticsImpl
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.logic.core.datastore.DatastoreFactory
import com.shub39.grit.logic.di.LogicModules
import com.shub39.grit.logic.habits.database.HabitDatabase
import com.shub39.grit.logic.habits.database.HabitDbFactory
import com.shub39.grit.logic.habits.database.HabitStatusDao
import com.shub39.grit.logic.habits.database.HabitsDao
import com.shub39.grit.logic.tasks.database.CategoryDao
import com.shub39.grit.logic.tasks.database.TaskDatabase
import com.shub39.grit.logic.tasks.database.TaskDbFactory
import com.shub39.grit.logic.tasks.database.TasksDao
import com.shub39.grit.shared.ui.di.UIModules
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Configuration
@Module(includes = [UIModules::class, LogicModules::class])
@ComponentScan("com.shub39.grit")
class GritModules {
    @Single fun provideAnalyticsWrapper(): AnalyticsWrapper = AnalyticsImpl()

    @Single fun getHabitDb(dbFactory: HabitDbFactory): HabitDatabase = dbFactory.create().build()

    @Single fun getTaskDb(dbFactory: TaskDbFactory): TaskDatabase = dbFactory.create().build()

    @Single fun getHabitDao(db: HabitDatabase): HabitsDao = db.habitDao()

    @Single fun getTaskDao(db: TaskDatabase): TasksDao = db.taskDao()

    @Single fun getHabitStatusDao(db: HabitDatabase): HabitStatusDao = db.habitStatusDao()

    @Single fun getCategoryDao(db: TaskDatabase): CategoryDao = db.categoryDao()

    @Single
    fun getDatastore(factory: DatastoreFactory): DataStore<Preferences> =
        factory.getPreferencesDataStore()
}
