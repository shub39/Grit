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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.shub39.grit.app.AppSections.Companion.toIconRes
import com.shub39.grit.app.AppSections.Companion.toStringRes
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.core.domain.MainAppState
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.navigation.fadeTransitionMetadata
import com.shub39.grit.core.navigation.verticalTransitionMetadata
import com.shub39.grit.core.presentation.ChangelogSheet
import com.shub39.grit.core.presentation.settings.ui.SettingsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksPage
import com.shub39.grit.core.utils.LocalWindowSizeClass
import com.shub39.grit.viewmodels.HabitViewModel
import com.shub39.grit.viewmodels.SettingsViewModel
import com.shub39.grit.viewmodels.TasksViewModel
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.alarm
import grit.shared.core.generated.resources.check_list
import grit.shared.core.generated.resources.habits
import grit.shared.core.generated.resources.settings
import grit.shared.core.generated.resources.tasks
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private sealed interface GlobalRoutes : NavKey {
    @Serializable data object PaywallPage : GlobalRoutes

    @Serializable data object App : GlobalRoutes
}

@Serializable
private sealed interface AppSections : NavKey {
    @Serializable data object HabitsPages : AppSections

    @Serializable data object TaskPages : AppSections

    @Serializable data object SettingsPages : AppSections

    companion object {
        val mainRoutes = listOf(TaskPages, HabitsPages, SettingsPages)

        fun AppSections.toStringRes(): StringResource {
            return when (this) {
                HabitsPages -> Res.string.habits
                TaskPages -> Res.string.tasks
                SettingsPages -> Res.string.settings
            }
        }

        fun AppSections.toIconRes(): DrawableResource {
            return when (this) {
                HabitsPages -> Res.drawable.alarm
                TaskPages -> Res.drawable.check_list
                SettingsPages -> Res.drawable.settings
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                    val windowSizeClass = LocalWindowSizeClass.current

                    val appBackStack =
                        rememberNavBackStack(
                            when (state.startingSection) {
                                Sections.Tasks -> AppSections.TaskPages
                                Sections.Habits -> AppSections.HabitsPages
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
                                                start =
                                                    padding.calculateStartPadding(
                                                        LocalLayoutDirection.current
                                                    ),
                                                end =
                                                    padding.calculateEndPadding(
                                                        LocalLayoutDirection.current
                                                    ),
                                                bottom = padding.calculateBottomPadding(),
                                            )
                                            .background(MaterialTheme.colorScheme.background),
                                    backStack = appBackStack,
                                    entryProvider =
                                        entryProvider {
                                            entry<AppSections.TaskPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val tvm: TasksViewModel = koinViewModel()
                                                val taskPageState by
                                                    tvm.state.collectAsStateWithLifecycle()

                                                TasksPage(
                                                    state = taskPageState,
                                                    onAction = tvm::onAction,
                                                )
                                            }

                                            entry<AppSections.SettingsPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val svm: SettingsViewModel = koinInject()
                                                val settingsState by
                                                    svm.state.collectAsStateWithLifecycle()

                                                SettingsGraph(
                                                    state = settingsState,
                                                    onAction = svm::onAction,
                                                    isUserSubscribed = state.isUserSubscribed,
                                                    onNavigateToPaywall = {
                                                        mainBackStack.add(GlobalRoutes.PaywallPage)
                                                    },
                                                )
                                            }

                                            entry<AppSections.HabitsPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val hvm: HabitViewModel = koinViewModel()
                                                val habitsPageState by
                                                    hvm.state.collectAsStateWithLifecycle()

                                                HabitsGraph(
                                                    state = habitsPageState,
                                                    onAction = hvm::onAction,
                                                    isUserSubscribed = state.isUserSubscribed,
                                                    onNavigateToPaywall = {
                                                        mainBackStack.add(GlobalRoutes.PaywallPage)
                                                    },
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
                                        Modifier.fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.background),
                                    backStack = appBackStack,
                                    contentAlignment = Alignment.Center,
                                    entryProvider =
                                        entryProvider {
                                            entry<AppSections.TaskPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val tvm: TasksViewModel = koinViewModel()
                                                val taskPageState by
                                                    tvm.state.collectAsStateWithLifecycle()

                                                TasksPage(
                                                    state = taskPageState,
                                                    onAction = tvm::onAction,
                                                )
                                            }

                                            entry<AppSections.SettingsPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val svm: SettingsViewModel = koinInject()
                                                val settingsState by
                                                    svm.state.collectAsStateWithLifecycle()

                                                SettingsGraph(
                                                    state = settingsState,
                                                    onAction = svm::onAction,
                                                    isUserSubscribed = state.isUserSubscribed,
                                                    onNavigateToPaywall = {
                                                        mainBackStack.add(GlobalRoutes.PaywallPage)
                                                    },
                                                )
                                            }

                                            entry<AppSections.HabitsPages>(
                                                metadata = fadeTransitionMetadata()
                                            ) {
                                                val hvm: HabitViewModel = koinViewModel()
                                                val habitsPageState by
                                                    hvm.state.collectAsStateWithLifecycle()

                                                HabitsGraph(
                                                    state = habitsPageState,
                                                    onAction = hvm::onAction,
                                                    isUserSubscribed = state.isUserSubscribed,
                                                    onNavigateToPaywall = {
                                                        mainBackStack.add(GlobalRoutes.PaywallPage)
                                                    },
                                                )
                                            }
                                        },
                                )
                            }
                        }
                    }
                }
            },
    )
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
