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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.core.LocalWindowSizeClass
import com.shub39.grit.core.app.AppSections
import com.shub39.grit.core.app.AppSections.Companion.toIconRes
import com.shub39.grit.core.app.AppSections.Companion.toStringRes
import com.shub39.grit.core.components.ChangelogSheet
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.navigation.fadeTransitionMetadata
import com.shub39.grit.core.navigation.verticalTransitionMetadata
import com.shub39.grit.core.settings.domain.Sections
import com.shub39.grit.core.settings.presentation.ui.SettingsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksPage
import com.shub39.grit.viewmodel.HabitViewModel
import com.shub39.grit.viewmodel.SettingsViewModel
import com.shub39.grit.viewmodel.TasksViewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private sealed interface GlobalRoutes : NavKey {
    @Serializable data object PaywallPage : GlobalRoutes

    @Serializable data object App : GlobalRoutes
}

@Composable
fun App(state: MainAppState, onRefreshSub: () -> Unit, onDismissChangelog: () -> Unit) {
    val mainBackStack = rememberNavBackStack(GlobalRoutes.App)

    if (state.currentChangelog != null) {
        ChangelogSheet(currentLog = state.currentChangelog, onDismissRequest = onDismissChangelog)
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

@Composable
private fun MainApp(state: MainAppState, onNavigateToPaywall: () -> Unit) {
    val windowSizeClass = LocalWindowSizeClass.current

    val appBackStack =
        rememberNavBackStack(
            when (state.startingSection) {
                Sections.Tasks -> AppSections.TaskPages
                Sections.Habits -> AppSections.HabitPages
            }
        )

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Scaffold(
                bottomBar = {
                    AppNavBar(
                        currentRoute = appBackStack.last(),
                        onNavigate = { route ->
                            appBackStack.removeAll { it == route }
                            appBackStack.add(route)
                        },
                    )
                }
            ) { padding ->
                NavDisplay(
                    modifier =
                        Modifier.padding(
                                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                                end = padding.calculateEndPadding(LocalLayoutDirection.current),
                                bottom = padding.calculateBottomPadding(),
                            )
                            .background(MaterialTheme.colorScheme.background),
                    backStack = appBackStack,
                    entryProvider =
                        entryProvider {
                            entry<AppSections.TaskPages>(metadata = fadeTransitionMetadata()) {
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(state = taskPageState, onAction = tvm::onAction)
                            }

                            entry<AppSections.SettingsPages>(metadata = fadeTransitionMetadata()) {
                                val svm: SettingsViewModel = koinViewModel()
                                val settingsState by svm.state.collectAsStateWithLifecycle()

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                    isUserSubscribed = state.isUserSubscribed,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                )
                            }

                            entry<AppSections.HabitPages>(metadata = fadeTransitionMetadata()) {
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                    isUserSubscribed = state.isUserSubscribed,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                )
                            }
                        },
                )
            }
        }

        else -> {
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                AppNavRail(
                    currentRoute = appBackStack.last(),
                    onNavigate = { route ->
                        appBackStack.removeAll { it == route }
                        appBackStack.add(route)
                    },
                )

                NavDisplay(
                    modifier =
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
                    backStack = appBackStack,
                    contentAlignment = Alignment.Center,
                    entryProvider =
                        entryProvider {
                            entry<AppSections.TaskPages>(metadata = fadeTransitionMetadata()) {
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(state = taskPageState, onAction = tvm::onAction)
                            }

                            entry<AppSections.SettingsPages>(metadata = fadeTransitionMetadata()) {
                                val svm: SettingsViewModel = koinViewModel()
                                val settingsState by svm.state.collectAsStateWithLifecycle()

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                    isUserSubscribed = state.isUserSubscribed,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                )
                            }

                            entry<AppSections.HabitPages>(metadata = fadeTransitionMetadata()) {
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                    isUserSubscribed = state.isUserSubscribed,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                )
                            }
                        },
                )
            }
        }
    }
}

@Composable
private fun AppNavRail(
    currentRoute: NavKey,
    onNavigate: (AppSections) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        AppSections.mainRoutes.forEach { route ->
            NavigationRailItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
                    }
                },
                icon = {
                    Icon(painter = painterResource(route.toIconRes()), contentDescription = null)
                },
                label = { Text(text = stringResource(route.toStringRes())) },
                alwaysShowLabel = false,
            )
        }
    }
}

@Composable
private fun AppNavBar(
    currentRoute: NavKey,
    onNavigate: (AppSections) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        AppSections.mainRoutes.forEach { route ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
                    }
                },
                icon = {
                    Icon(painter = painterResource(route.toIconRes()), contentDescription = null)
                },
                label = { Text(text = stringResource(route.toStringRes())) },
                alwaysShowLabel = false,
            )
        }
    }
}
