package com.shub39.grit.tasks.presentation.tasks_settings

sealed interface TasksSettingsAction {
    data class UpdateClearPreference(val clearPreference: String): TasksSettingsAction
}