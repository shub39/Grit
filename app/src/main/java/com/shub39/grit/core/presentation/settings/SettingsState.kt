package com.shub39.grit.core.presentation.settings

import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.theme.Theme
import java.time.DayOfWeek

data class SettingsState(
    val theme: Theme = Theme(),
    val is24Hr: Boolean = false,
    val startOfTheWeek: DayOfWeek = DayOfWeek.MONDAY,
    val pauseNotifications: Boolean = false,
    val startingPage: Pages = Pages.Tasks,
    val backupState: BackupState = BackupState(),
    val biometric: Boolean = false
)

data class BackupState(
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE
)