package com.shub39.grit.app

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {
    @Serializable
    data object HabitsPage: Routes

    @Serializable
    data object  TasksPage: Routes

    @Serializable
    data object SettingsGraph: Routes
    @Serializable
    data object Settings: Routes
    @Serializable
    data object About: Routes
    @Serializable
    data object Categories: Routes
    @Serializable
    data object LookAndFeel: Routes
}