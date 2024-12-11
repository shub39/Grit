package com.shub39.grit.tasks.presentation.tasks_settings

import com.shub39.grit.tasks.domain.Category

sealed interface TasksSettingsAction {
    data class UpdateClearPreference(val clearPreference: String): TasksSettingsAction
    data class DeleteCategory(val category: Category): TasksSettingsAction
}