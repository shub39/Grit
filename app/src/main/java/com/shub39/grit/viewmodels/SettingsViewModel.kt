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
package com.shub39.grit.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.data.ChangelogManager
import com.shub39.grit.core.data.Utils
import com.shub39.grit.core.domain.SettingsDatastore
import com.shub39.grit.core.domain.ThemeDatastore
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.core.domain.backup.RestoreResult
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.settings.BackupState
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val exportRepo: ExportRepo,
    private val restoreRepo: RestoreRepo,
    private val themeDatastore: ThemeDatastore,
    private val settingsDatastore: SettingsDatastore,
    private val changelogManager: ChangelogManager,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(SettingsState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                observeJob()
                getChangeLogs()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onAction(action: SettingsAction) =
        viewModelScope.launch {
            when (action) {
                is SettingsAction.ChangeAmoled -> themeDatastore.setAmoledPref(action.pref)

                is SettingsAction.ChangeAppTheme -> themeDatastore.setAppTheme(action.appTheme)

                is SettingsAction.ChangeIs24Hr -> settingsDatastore.setIs24Hr(action.pref)

                is SettingsAction.ChangeMaterialYou -> themeDatastore.setMaterialYou(action.pref)

                is SettingsAction.ChangePaletteStyle -> themeDatastore.setPaletteStyle(action.style)

                is SettingsAction.ChangeSeedColor ->
                    themeDatastore.setSeedColor(action.color.toArgb())

                is SettingsAction.ChangeStartOfTheWeek ->
                    settingsDatastore.setStartOfWeek(action.pref)

                is SettingsAction.ChangeStartingPage ->
                    settingsDatastore.setStartingPage(action.page)

                SettingsAction.OnResetBackupState -> {
                    _state.update { it.copy(backupState = BackupState()) }
                }

                SettingsAction.OnExport -> {
                    _state.update {
                        it.copy(
                            backupState = it.backupState.copy(exportState = ExportState.EXPORTING)
                        )
                    }

                    exportRepo.exportToJson()

                    _state.update {
                        it.copy(
                            backupState = it.backupState.copy(exportState = ExportState.EXPORTED)
                        )
                    }
                }

                is SettingsAction.OnRestore -> {
                    _state.update {
                        it.copy(
                            backupState = it.backupState.copy(restoreState = RestoreState.RESTORING)
                        )
                    }

                    val result = restoreRepo.restoreData(action.uri)

                    _state.update {
                        it.copy(
                            backupState =
                                it.backupState.copy(
                                    restoreState =
                                        when (result) {
                                            is RestoreResult.Failure -> RestoreState.FAILURE
                                            RestoreResult.Success -> RestoreState.RESTORED
                                        }
                                )
                        )
                    }
                }

                is SettingsAction.ChangePauseNotifications ->
                    settingsDatastore.setNotifications(action.pref)

                is SettingsAction.ChangeFontPref -> themeDatastore.setFontPref(action.font)

                is SettingsAction.ChangeBiometricLock ->
                    settingsDatastore.setBiometricPref(action.pref)

                is SettingsAction.OnCheckBiometric -> {
                    _state.update {
                        it.copy(
                            isBiometricLockAvailable = Utils.authenticationAvailable(action.context)
                        )
                    }
                }

                is SettingsAction.ChangeReorderTasks ->
                    settingsDatastore.setTaskReorderPref(action.pref)
            }
        }

    private fun getChangeLogs() {
        viewModelScope.launch {
            _state.update { it.copy(changelog = changelogManager.changelogs.first()) }
        }
    }

    private fun observeJob() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                settingsDatastore
                    .getTaskReorderPref()
                    .onEach { pref -> _state.update { it.copy(reorderTasks = pref) } }
                    .launchIn(this)

                settingsDatastore
                    .getNotificationsFlow()
                    .onEach { pref -> _state.update { it.copy(pauseNotifications = pref) } }
                    .launchIn(this)

                themeDatastore
                    .getAppThemeFlow()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(appTheme = flow)) }
                    }
                    .launchIn(this)

                themeDatastore
                    .getFontPrefFlow()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(font = flow)) }
                    }
                    .launchIn(this)

                themeDatastore
                    .getSeedColorFlow()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(seedColor = Color(flow))) }
                    }
                    .launchIn(this)

                themeDatastore
                    .getAmoledPref()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(isAmoled = flow)) }
                    }
                    .launchIn(this)

                themeDatastore
                    .getMaterialYouFlow()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(isMaterialYou = flow)) }
                    }
                    .launchIn(this)

                themeDatastore
                    .getPaletteStyle()
                    .onEach { flow ->
                        _state.update { it.copy(theme = it.theme.copy(paletteStyle = flow)) }
                    }
                    .launchIn(this)

                settingsDatastore
                    .getIs24Hr()
                    .onEach { flow -> _state.update { it.copy(is24Hr = flow) } }
                    .launchIn(this)

                settingsDatastore
                    .getStartingSectionPref()
                    .onEach { flow -> _state.update { it.copy(startingPage = flow) } }
                    .launchIn(this)

                settingsDatastore
                    .getStartOfTheWeekPref()
                    .onEach { flow -> _state.update { it.copy(startOfTheWeek = flow) } }
                    .launchIn(this)

                settingsDatastore
                    .getBiometricLockPref()
                    .onEach { flow -> _state.update { it.copy(isBiometricLockOn = flow) } }
                    .launchIn(this)
            }
        }
}
