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
package com.shub39.grit.shared.ui.viewmodel

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.core.interfaces.BiometricUtils
import com.shub39.grit.core.interfaces.ChangelogManager
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.interfaces.ThemeDatastore
import com.shub39.grit.core.settings.backup.ExportRepo
import com.shub39.grit.core.settings.backup.RestoreRepo
import com.shub39.grit.shared.ui.setting.BackupState
import com.shub39.grit.shared.ui.setting.SettingsAction
import com.shub39.grit.shared.ui.setting.SettingsState
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
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SettingsViewModel(
    @Provided private val exportRepo: ExportRepo,
    @Provided private val restoreRepo: RestoreRepo,
    @Provided private val themeDatastore: ThemeDatastore,
    @Provided private val settingsDatastore: SettingsDatastore,
    @Provided private val changelogManager: ChangelogManager,
    @Provided private val biometricUtils: BiometricUtils,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(SettingsState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                observeJob()
                getChangeLogs()
                getBiometricStatus()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onAction(action: SettingsAction) =
        viewModelScope.launch {
            when (action) {
                is ChangeAmoled -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangeAmoled", "value" to action.pref),
                    )
                    themeDatastore.setAmoledPref(action.pref)
                }

                is ChangeAppTheme -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangeAppTheme", "value" to action.appTheme.name),
                    )
                    themeDatastore.setAppTheme(action.appTheme)
                }

                is ChangeIs24Hr -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangeIs24Hr", "value" to action.pref),
                    )
                    settingsDatastore.setIs24Hr(action.pref)
                }

                is ChangeMaterialYou -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangeMaterialYou", "value" to action.pref),
                    )
                    themeDatastore.setMaterialYou(action.pref)
                }

                is ChangePaletteStyle -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangePaletteStyle", "value" to action.style.name),
                    )
                    themeDatastore.setPaletteStyle(action.style)
                }

                is ChangeSeedColor -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangeSeedColor"),
                    )
                    themeDatastore.setSeedColor(action.color.toArgb())
                }

                is ChangeStartOfTheWeek -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangeStartOfTheWeek", "value" to action.pref.name),
                    )
                    settingsDatastore.setStartOfWeek(action.pref)
                }

                is ChangeStartingPage -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangeStartingPage", "value" to action.page.name),
                    )
                    settingsDatastore.setStartingPage(action.page)
                }

                OnResetBackupState -> {
                    _state.update { it.copy(backupState = BackupState()) }
                }

                OnExport -> {
                    _state.update {
                        it.copy(backupState = it.backupState.copy(exportState = EXPORTING))
                    }

                    exportRepo.exportToJson()
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.BACKUP_CREATED.name,
                        mapOf("status" to "success"),
                    )

                    _state.update {
                        it.copy(backupState = it.backupState.copy(exportState = EXPORTED))
                    }
                }

                OnRestore -> {
                    _state.update {
                        it.copy(backupState = it.backupState.copy(restoreState = RESTORING))
                    }

                    val result = restoreRepo.restoreData()
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.BACKUP_RESTORED.name,
                        mapOf("status" to result.toString()),
                    )

                    _state.update {
                        it.copy(
                            backupState =
                                it.backupState.copy(
                                    restoreState =
                                        when (result) {
                                            is Failure -> FAILURE
                                            Success -> RESTORED
                                        }
                                )
                        )
                    }
                }

                is ChangePauseNotifications -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangePauseNotifications", "value" to action.pref),
                    )
                    settingsDatastore.setNotifications(action.pref)
                }

                is ChangeFontPref -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.LOOK_AND_FEEL_UPDATED.name,
                        mapOf("setting" to "ChangeFontPref", "value" to action.font.name),
                    )
                    themeDatastore.setFontPref(action.font)
                }

                is ChangeBiometricLock -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangeBiometricLock", "value" to action.pref),
                    )
                    settingsDatastore.setBiometricPref(action.pref)
                }

                is ChangeReorderTasks -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_UPDATED.name,
                        mapOf("setting" to "ChangeReorderTasks", "value" to action.pref),
                    )
                    settingsDatastore.setTaskReorderPref(action.pref)
                }

                OnSettingsOpened -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.SETTINGS_OPENED.name,
                        emptyMap(),
                    )
                }

                OnAboutViewed -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.ABOUT_VIEWED.name,
                        emptyMap(),
                    )
                }

                OnChangelogViewed -> {
                    analytics.trackEvent(
                        AnalyticsWrapper.Companion.AnalyticsEvent.CHANGELOG_VIEWED.name,
                        emptyMap(),
                    )
                }
            }
        }

    private fun getBiometricStatus() {
        _state.update {
            it.copy(isBiometricLockAvailable = biometricUtils.authenticationAvailable())
        }
    }

    private suspend fun getChangeLogs() {
        val currentChangelog = changelogManager.changelogs.first()
        _state.update {
            it.copy(
                changelog = currentChangelog,
                currentVersion = currentChangelog.firstOrNull()?.version,
            )
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
                        _state.update { it.copy(theme = it.theme.copy(seedColor = flow)) }
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
