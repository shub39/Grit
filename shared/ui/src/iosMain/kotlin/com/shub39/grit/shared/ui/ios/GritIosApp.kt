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
package com.shub39.grit.shared.ui.ios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.logic.ios.GritSdk
import com.shub39.grit.shared.ui.components.FossPaywall
import com.shub39.grit.shared.ui.components.InitialLoading
import com.shub39.grit.shared.ui.navigation.GritNavDisplay
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.viewmodel.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * iOS counterpart of `MainActivity`'s content: the biometric gate, the theme from the datastore and
 * the shared navigation graph. The paywall is the FOSS one until StoreKit is wired up.
 */
@Composable
fun GritIosApp() {
    val mainViewModel = koinViewModel<MainViewModel>()
    val state by mainViewModel.state.collectAsStateWithLifecycle()

    var showContent by remember { mutableStateOf(false) }
    var askedForAuthentication by remember { mutableStateOf(false) }

    LaunchedEffect(state.isAppUnlocked, state.isBiometricLockOn) {
        val isLockOn = state.isBiometricLockOn ?: return@LaunchedEffect

        when {
            !isLockOn || state.isAppUnlocked -> showContent = true
            askedForAuthentication -> Unit
            !GritSdk.biometricsAvailable() -> {
                mainViewModel.setBiometricLock(false)
                showContent = true
            }
            else -> {
                askedForAuthentication = true
                GritSdk.authenticate("Unlock Grit") { success ->
                    if (success) mainViewModel.setAppUnlocked(true)
                    showContent = true
                }
            }
        }
    }

    if (showContent) {
        GritNavDisplay(paywall = { _, _ -> FossPaywall() })
    } else {
        GritTheme(theme = state.theme) { InitialLoading() }
    }
}
