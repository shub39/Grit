package com.shub39.grit.core.presentation.settings

import androidx.compose.runtime.Immutable
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.ClearPreferences

@Immutable
data class SettingsState(
    val currentClearPreference: String = ClearPreferences.NEVER.value,
    val categories: List<Category> = emptyList()
)
