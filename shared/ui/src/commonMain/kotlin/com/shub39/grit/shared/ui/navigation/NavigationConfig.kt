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
package com.shub39.grit.shared.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

interface Routes : NavKey

data object Paywall : Routes

data object Tasks : Routes

data object TaskList : Routes

data object Habits : Routes

data object HabitsList : Routes

data object HabitAnalytics : Routes

data object OverallAnalytics : Routes

data object Calendar : Routes

data object CalendarHeatMap : Routes

data object Settings : Routes

data object SettingsHome : Routes

data object About : Routes

data object Changelog : Routes

data object Backup : Routes

data object LookAndFeel : Routes

private val SETTINGS_ROUTES = listOf(About, Backup, Changelog, LookAndFeel, SettingsHome)
private val HABIT_ROUTES =
    listOf(HabitAnalytics, HabitsList, OverallAnalytics, Calendar, CalendarHeatMap)

fun Routes.getTopLevelRoute(): Routes? =
    when (val subType = this.getSubType()) {
        Tasks,
        Habits,
        Settings -> subType
        else -> null
    }

fun Routes.getSubType(): Routes =
    when (this) {
        in HABIT_ROUTES -> Habits
        in SETTINGS_ROUTES -> Settings
        TaskList -> Tasks
        else -> this
    }

class TopLevelBackStack(private val startKey: Routes = TaskList) {
    val backStack = mutableStateListOf(startKey)

    fun isRouteOnTop(route: Routes): Boolean = backStack.lastOrNull() == route

    fun add(route: Routes) {
        if (!isRouteOnTop(route)) {
            backStack.clear()
            backStack.add(startKey)

            if (route.getTopLevelRoute() is Settings) {
                backStack.add(SettingsHome)
                if (route !is SettingsHome) backStack.add(route)
                return
            }

            if (route.getTopLevelRoute() is Habits) {
                backStack.removeAll(HABIT_ROUTES)
                backStack.add(HabitsList)
                if (route is HabitsList) return
                when (route) {
                    HabitAnalytics -> backStack.add(HabitAnalytics)
                    OverallAnalytics -> backStack.add(OverallAnalytics)
                    Calendar -> {
                        backStack.add(HabitAnalytics)
                        backStack.add(Calendar)
                    }
                    CalendarHeatMap -> {
                        backStack.add(OverallAnalytics)
                        backStack.add(CalendarHeatMap)
                    }
                    else -> backStack.add(route)
                }
                return
            }

            backStack.add(route)
        }
    }

    fun removeLast() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
}
