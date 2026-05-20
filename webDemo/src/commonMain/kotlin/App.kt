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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.shub39.grit.core.LocalWindowSizeClass
import com.shub39.grit.core.app.AppSections
import com.shub39.grit.core.app.AppSections.Companion.toIconRes
import com.shub39.grit.core.app.AppSections.Companion.toStringRes
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.navigation.fadeTransitionMetadata
import com.shub39.grit.core.settings.presentation.SettingsAction
import com.shub39.grit.core.settings.presentation.ui.SettingsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksPage
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import grit.webdemo.generated.resources.Res
import grit.webdemo.generated.resources.dark_mode
import grit.webdemo.generated.resources.light_mode
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private val configuration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppSections.TaskPages::class, AppSections.TaskPages.serializer())
            subclass(AppSections.HabitPages::class, AppSections.HabitPages.serializer())
            subclass(AppSections.SettingsPages::class, AppSections.SettingsPages.serializer())
        }
    }
}

@Composable
fun App() {
    val backStack = rememberNavBackStack(configuration, AppSections.TaskPages)
    val settingsState by DummyStateProvider.settingsState.collectAsState()
    val windowSizeClass = LocalWindowSizeClass.current

    GritTheme(theme = settingsState.theme) {
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            AppSections.mainRoutes.forEach { route: AppSections ->
                                NavigationBarItem(
                                    selected = backStack.last() == route,
                                    onClick = {
                                        if (backStack.last() != route) {
                                            backStack.removeAll { it == route }
                                            backStack.add(route)
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = vectorResource(route.toIconRes()),
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(text = stringResource(route.toStringRes())) },
                                    alwaysShowLabel = true,
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    NavDisplay(
                        modifier = Modifier.padding(paddingValues),
                        backStack = backStack,
                        entryProvider =
                            entryProvider {
                                entry<AppSections.TaskPages>(metadata = fadeTransitionMetadata()) {
                                    val state by DummyStateProvider.taskState.collectAsState()

                                    TasksPage(
                                        state = state,
                                        onAction = DummyStateProvider::onTaskAction,
                                    )
                                }

                                entry<AppSections.HabitPages>(metadata = fadeTransitionMetadata()) {
                                    val state by DummyStateProvider.habitState.collectAsState()

                                    HabitsGraph(
                                        state = state,
                                        onAction = DummyStateProvider::onHabitAction,
                                        isUserSubscribed = true,
                                        onNavigateToPaywall = {},
                                    )
                                }

                                entry<AppSections.SettingsPages> {
                                    val state by DummyStateProvider.settingsState.collectAsState()

                                    SettingsGraph(
                                        state = state,
                                        onAction = DummyStateProvider::onSettingsAction,
                                        isUserSubscribed = true,
                                        onNavigateToPaywall = {},
                                    )
                                }
                            },
                    )
                }
            }

            else -> {
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    NavigationRail(
                        header = {
                            FloatingActionButton(
                                onClick = {
                                    val newTheme =
                                        if (settingsState.theme.appTheme == AppTheme.DARK) {
                                            AppTheme.LIGHT
                                        } else {
                                            AppTheme.DARK
                                        }
                                    DummyStateProvider.onSettingsAction(
                                        SettingsAction.ChangeAppTheme(newTheme)
                                    )
                                },
                                modifier = Modifier.padding(top = 16.dp),
                            ) {
                                Icon(
                                    imageVector =
                                        vectorResource(
                                            if (settingsState.theme.appTheme == AppTheme.DARK) {
                                                Res.drawable.light_mode
                                            } else {
                                                Res.drawable.dark_mode
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        modifier =
                            Modifier.clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)),
                    ) {
                        AppSections.mainRoutes.forEach { route: AppSections ->
                            NavigationRailItem(
                                selected = backStack.last() == route,
                                onClick = {
                                    if (backStack.last() != route) {
                                        backStack.removeAll { it == route }
                                        backStack.add(route)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = vectorResource(route.toIconRes()),
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(text = stringResource(route.toStringRes())) },
                                alwaysShowLabel = true,
                            )
                        }
                    }

                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                        entryProvider =
                            entryProvider {
                                entry<AppSections.TaskPages>(metadata = fadeTransitionMetadata()) {
                                    val state by DummyStateProvider.taskState.collectAsState()

                                    TasksPage(
                                        state = state,
                                        onAction = DummyStateProvider::onTaskAction,
                                    )
                                }

                                entry<AppSections.HabitPages>(metadata = fadeTransitionMetadata()) {
                                    val state by DummyStateProvider.habitState.collectAsState()

                                    HabitsGraph(
                                        state = state,
                                        onAction = DummyStateProvider::onHabitAction,
                                        isUserSubscribed = true,
                                        onNavigateToPaywall = {},
                                    )
                                }

                                entry<AppSections.SettingsPages> {
                                    val state by DummyStateProvider.settingsState.collectAsState()

                                    SettingsGraph(
                                        state = state,
                                        onAction = DummyStateProvider::onSettingsAction,
                                        isUserSubscribed = true,
                                        onNavigateToPaywall = {},
                                    )
                                }
                            },
                    )
                }
            }
        }
    }
}
