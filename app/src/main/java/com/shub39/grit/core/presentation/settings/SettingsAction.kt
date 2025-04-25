package com.shub39.grit.core.presentation.settings

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Pages
import java.time.DayOfWeek

sealed interface SettingsAction {
    data object OnResetBackupState : SettingsAction
    data object OnExport : SettingsAction
    data class OnRestore(val uri: Uri) : SettingsAction

    data class ChangeStartOfTheWeek(val pref: DayOfWeek) : SettingsAction
    data class ChangeIs24Hr(val pref: Boolean) : SettingsAction
    data class ChangeStartingPage(val page: Pages) : SettingsAction

    data class ChangeAppTheme(val appTheme: AppTheme): SettingsAction
    data class ChangeSeedColor(val color: Color) : SettingsAction
    data class ChangeAmoled(val pref: Boolean) : SettingsAction
    data class ChangePaletteStyle(val style: PaletteStyle) : SettingsAction
    data class ChangeMaterialYou(val pref: Boolean) : SettingsAction
}