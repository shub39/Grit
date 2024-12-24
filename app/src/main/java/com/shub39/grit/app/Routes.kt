package com.shub39.grit.app

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {
    @Serializable
    data object HabitsPage: Routes

    @Serializable
    data object  TasksPage: Routes

    @Serializable
    data object Settings: Routes
}