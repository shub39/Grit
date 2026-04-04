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
package com.shub39.grit.core.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.core.navigation.horizontalTransitionMetadata
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.section.BackupPage
import com.shub39.grit.core.presentation.settings.ui.section.Changelog
import com.shub39.grit.core.presentation.settings.ui.section.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.ui.section.RootPage
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import kotlinx.serialization.Serializable

@Serializable data object Root : NavKey

@Serializable data object LookAndFeel : NavKey

@Serializable data object Backup : NavKey

@Serializable data object Changelog : NavKey

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    isUserSubscribed: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val backStack = rememberNavBackStack(Root)

        NavDisplay(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.background)
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
            backStack = backStack,
            entryProvider =
                entryProvider {
                    entry<Root> {
                        RootPage(
                            state = state,
                            onAction = onAction,
                            onNavigateToLookAndFeel = { backStack.add(LookAndFeel) },
                            onNavigateToBackup = { backStack.add(Backup) },
                            onNavigateToPaywall = onNavigateToPaywall,
                            onNavigateToChangelog = { backStack.add(Changelog) },
                        )
                    }

                    entry<LookAndFeel>(metadata = horizontalTransitionMetadata()) {
                        LookAndFeelPage(
                            state = state,
                            onAction = onAction,
                            isUserSubscribed = isUserSubscribed,
                            onNavigateToPaywall = onNavigateToPaywall,
                            onNavigateBack = {
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                        )
                    }

                    entry<Backup>(metadata = horizontalTransitionMetadata()) {
                        BackupPage(
                            state = state,
                            onAction = onAction,
                            onNavigateBack = {
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                        )
                    }

                    entry<Changelog>(metadata = horizontalTransitionMetadata()) {
                        Changelog(
                            changelog = state.changelog,
                            onNavigateBack = {
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                        )
                    }
                },
        )
    }

@Preview
@Composable
private fun Preview() {
    GritTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        SettingsGraph(
            state = SettingsState(),
            onAction = {},
            onNavigateToPaywall = {},
            isUserSubscribed = true,
        )
    }
}
