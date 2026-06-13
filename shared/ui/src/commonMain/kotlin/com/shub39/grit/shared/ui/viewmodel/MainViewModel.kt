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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.interfaces.ChangelogManager
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.interfaces.ThemeDatastore
import com.shub39.grit.shared.ui.app.MainAppState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
class MainViewModel(
    @Provided private val themeDatastore: ThemeDatastore,
    @Provided private val settingsDatastore: SettingsDatastore,
    @Provided private val billingHandler: BillingHandler,
    @Provided private val changelogManager: ChangelogManager,
) : ViewModel() {
    var observerJob: Job? = null

    private val _state = MutableStateFlow(MainAppState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                checkSubscription()
                checkChangelog()
                observeDatastore()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = MainAppState(),
            )

    fun setAppUnlocked(value: Boolean) {
        _state.update { it.copy(isAppUnlocked = value) }
    }

    fun setBiometricLock(value: Boolean) {
        viewModelScope.launch { settingsDatastore.setBiometricPref(value) }
    }

    fun updateSubscription() {
        viewModelScope.launch { checkSubscription() }
    }

    private fun observeDatastore() {
        observerJob?.cancel()
        observerJob =
            viewModelScope.launch {
                combine(
                        themeDatastore.getPaletteStyle(),
                        themeDatastore.getSeedColorFlow(),
                        themeDatastore.getFontPrefFlow(),
                        themeDatastore.getMaterialYouFlow(),
                        themeDatastore.getAppThemeFlow(),
                    ) { palette, seedColor, font, materialYou, appTheme ->
                        _state.update {
                            it.copy(
                                theme =
                                    it.theme.copy(
                                        paletteStyle = palette,
                                        seedColor = seedColor,
                                        font = font,
                                        isMaterialYou = materialYou,
                                        appTheme = appTheme,
                                    )
                            )
                        }
                    }
                    .launchIn(this)

                themeDatastore
                    .getAmoledPref()
                    .onEach { pref ->
                        _state.update { it.copy(theme = it.theme.copy(isAmoled = pref)) }
                    }
                    .launchIn(this)

                settingsDatastore
                    .getStartingSectionPref()
                    .onEach { pref -> _state.update { it.copy(startingSection = pref) } }
                    .launchIn(this)

                settingsDatastore
                    .getBiometricLockPref()
                    .onEach { pref -> _state.update { it.copy(isBiometricLockOn = pref) } }
                    .launchIn(this)
            }
    }

    private fun checkChangelog() {
        viewModelScope.launch {
            val changeLogs = changelogManager.changelogs.first()
            val lastShownChangelog = settingsDatastore.getLastChangelogShown().first()

            if (lastShownChangelog != changeLogs.firstOrNull()?.version) {
                _state.update { it.copy(currentChangelog = changeLogs.firstOrNull()) }
            }
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            Subscribed -> {
                _state.update { it.copy(isUserSubscribed = true) }
            }
            else -> {}
        }
    }

    fun dismissChangelog() {
        _state.value.currentChangelog?.version?.let {
            viewModelScope.launch { settingsDatastore.updateLastChangelogShown(it) }
        }
        _state.update { it.copy(currentChangelog = null) }
    }
}
