package com.shub39.grit.app

import kotlinx.serialization.Serializable

sealed interface MainRoutes {
    @Serializable
    data object HabitsPages: MainRoutes

    @Serializable
    data object  TaskPages: MainRoutes

    @Serializable
    data object SettingsPages: MainRoutes

    companion object {
        val allRoutes = listOf(
            TaskPages,
            HabitsPages,
            SettingsPages
        )
    }
}