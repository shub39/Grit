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
package com.shub39.grit.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.shared.ui.app.MainApp
import com.shub39.grit.shared.ui.app.MainAppState
import com.shub39.grit.shared.ui.components.ChangelogSheet
import com.shub39.grit.shared.ui.navigation.verticalTransitionMetadata
import kotlinx.serialization.Serializable

private sealed interface GlobalRoutes : NavKey {
    @Serializable data object PaywallPage : GlobalRoutes

    @Serializable data object App : GlobalRoutes
}

@Composable
fun App(state: MainAppState, onRefreshSub: () -> Unit, onDismissChangelog: () -> Unit) {
    val mainBackStack = rememberNavBackStack(GlobalRoutes.App)

    if (state.currentChangelog != null) {
        ChangelogSheet(currentLog = state.currentChangelog!!, onDismissRequest = onDismissChangelog)
    }

    NavDisplay(
        backStack = mainBackStack,
        entryProvider =
            entryProvider {
                entry<GlobalRoutes.PaywallPage>(metadata = verticalTransitionMetadata()) {
                    DisposableEffect(Unit) { onDispose { onRefreshSub() } }

                    PaywallPage(
                        isPlusUser = state.isUserSubscribed,
                        onDismissRequest = {
                            if (mainBackStack.size != 1) mainBackStack.removeLastOrNull()
                        },
                    )
                }

                entry<GlobalRoutes.App> {
                    MainApp(
                        state = state,
                        onNavigateToPaywall = { mainBackStack.add(GlobalRoutes.PaywallPage) },
                    )
                }
            },
    )
}
