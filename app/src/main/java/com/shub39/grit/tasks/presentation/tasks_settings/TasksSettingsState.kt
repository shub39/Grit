package com.shub39.grit.tasks.presentation.tasks_settings

import androidx.compose.runtime.Immutable
import com.shub39.grit.tasks.domain.ClearPreferences

@Immutable
data class TasksSettingsState(
    val currentClearPreference: String = ClearPreferences.NEVER.value
)
