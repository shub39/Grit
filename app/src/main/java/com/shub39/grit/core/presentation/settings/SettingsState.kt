package com.shub39.grit.core.presentation.settings

import com.shub39.grit.core.domain.Pages

data class SettingsState(
    val startingPage: Pages = Pages.Tasks
)