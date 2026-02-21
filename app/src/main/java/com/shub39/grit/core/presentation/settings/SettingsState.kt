/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.presentation.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.domain.Changelog
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.theme.Theme
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
    val changelog: Changelog = emptyList(),
)

@Stable
@Immutable
data class BackupState(
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE,
)
