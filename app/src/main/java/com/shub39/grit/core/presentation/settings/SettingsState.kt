package com.shub39.grit.core.presentation.settings

import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.theme.Theme
import java.time.DayOfWeek

data class SettingsState(
    val theme: Theme = Theme(),
    val is24Hr: Boolean = false,
    val startOfTheWeek: DayOfWeek = DayOfWeek.MONDAY,
    val startingPage: Pages = Pages.Tasks
)