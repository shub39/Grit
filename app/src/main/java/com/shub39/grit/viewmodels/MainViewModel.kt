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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.BuildConfig
import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.billing.SubscriptionResult
import com.shub39.grit.core.data.ChangelogManager
import com.shub39.grit.core.domain.MainAppState
import com.shub39.grit.core.domain.SettingsDatastore
import com.shub39.grit.core.domain.ThemeDatastore
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
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val themeDatastore: ThemeDatastore,
    private val settingsDatastore: SettingsDatastore,
    private val billingHandler: BillingHandler,
    private val changelogManager: ChangelogManager,
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
                                        seedColor = Color(seedColor),
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

            if (BuildConfig.DEBUG || lastShownChangelog != BuildConfig.VERSION_NAME) {
                _state.update { it.copy(currentChangelog = changeLogs.firstOrNull()) }
            }
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                _state.update { it.copy(isUserSubscribed = true) }
            }
            else -> {}
        }
    }

    fun dismissChangelog() {
        _state.update { it.copy(currentChangelog = null) }
        viewModelScope.launch {
            settingsDatastore.updateLastChangelogShown(BuildConfig.VERSION_NAME)
        }
    }
}
