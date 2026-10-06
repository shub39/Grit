package com.shub39.grit.shared.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

sealed interface Routes: NavKey {
    data object Paywall: Routes

    sealed interface TaskPages : Routes {
        data object TaskList : TaskPages
    }

    sealed interface HabitPages : Routes {
        data object HabitList : HabitPages
        data object HabitAnalytics : HabitPages
        data object OverallAnalytics : HabitPages
        data object Calendar : HabitPages
        data object CalendarHeatMap : HabitPages
    }

    sealed interface SettingsPages : Routes {
        data object Settings : SettingsPages
        data object About : SettingsPages
        data object Changelog : SettingsPages
        data object Backup : SettingsPages
        data object LookAndFeel : SettingsPages
    }
}

class TopLevelBackStack(startKey: Routes = Routes.TaskPages.TaskList) {
    val backStack = mutableStateListOf(startKey)

    fun Routes.getSubType() = when (this) {
        Routes.HabitPages.HabitAnalytics -> Routes.HabitPages::class
        Routes.HabitPages.HabitList -> Routes.HabitPages::class
        Routes.HabitPages.OverallAnalytics -> Routes.HabitPages::class
        Routes.HabitPages.Calendar -> Routes.HabitPages::class
        Routes.HabitPages.CalendarHeatMap -> Routes.HabitPages::class

        Paywall -> this

        Routes.SettingsPages.About -> Routes.SettingsPages::class
        Routes.SettingsPages.Backup -> Routes.SettingsPages::class
        Routes.SettingsPages.Changelog -> Routes.SettingsPages::class
        Routes.SettingsPages.LookAndFeel -> Routes.SettingsPages::class
        Routes.SettingsPages.Settings -> Routes.SettingsPages::class

        Routes.TaskPages.TaskList -> Routes.TaskPages::class
    }

    fun isRouteOnTop(route: Routes): Boolean = backStack.lastOrNull() == route

    fun add(route: Routes) {
        if (!isRouteOnTop(route)) backStack.add(route)
    }

    fun removeLast() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
}