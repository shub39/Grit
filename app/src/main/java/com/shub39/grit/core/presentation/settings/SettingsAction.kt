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

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import kotlinx.datetime.DayOfWeek

sealed interface SettingsAction {
    data object OnResetBackupState : SettingsAction

    data object OnExport : SettingsAction

    data class OnRestore(val uri: Uri) : SettingsAction

    data class OnCheckBiometric(val context: Context) : SettingsAction

    data class ChangeStartOfTheWeek(val pref: DayOfWeek) : SettingsAction

    data class ChangeIs24Hr(val pref: Boolean) : SettingsAction

    data class ChangeStartingPage(val page: Sections) : SettingsAction

    data class ChangePauseNotifications(val pref: Boolean) : SettingsAction

    data class ChangeReorderTasks(val pref: Boolean) : SettingsAction

    data class ChangeAppTheme(val appTheme: AppTheme) : SettingsAction

    data class ChangeFontPref(val font: Fonts) : SettingsAction

    data class ChangeSeedColor(val color: Color) : SettingsAction

    data class ChangeAmoled(val pref: Boolean) : SettingsAction

    data class ChangePaletteStyle(val style: PaletteStyle) : SettingsAction

    data class ChangeMaterialYou(val pref: Boolean) : SettingsAction

    data class ChangeBiometricLock(val pref: Boolean) : SettingsAction
}
