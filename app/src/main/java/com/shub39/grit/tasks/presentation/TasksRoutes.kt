package com.shub39.grit.tasks.presentation

import kotlinx.serialization.Serializable

sealed interface TasksRoutes {
    @Serializable
    data object TasksList: TasksRoutes

    @Serializable
    data object EditCategories: TasksRoutes
}