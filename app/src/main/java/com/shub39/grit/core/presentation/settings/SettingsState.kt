package com.shub39.grit.core.presentation.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.theme.Theme
import kotlinx.datetime.DayOfWeek

@Stable
@Immutable
data class SettingsState(
    val theme: Theme = Theme(),
    val is24Hr: Boolean = false,
    val reorderTasks: Boolean = false,
    val startOfTheWeek: DayOfWeek = DayOfWeek.MONDAY,
    val pauseNotifications: Boolean = false,
    val startingPage: Sections = Sections.Tasks,
    val backupState: BackupState = BackupState(),
    val isBiometricLockOn: Boolean? = null,
    val isBiometricLockAvailable: Boolean = false,
    val showPaywall: Boolean = false,
    val isUserSubscribed: Boolean = false,
    val serverState: ServerState = ServerState()
)

@Stable
@Immutable
data class ServerState(
    val isRunning: Boolean = false,
    val serverPort: Int = 8080,
    val serverUrl: String? = null
)

@Stable
@Immutable
data class BackupState(
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE
)