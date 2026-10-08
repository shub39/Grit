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
package com.shub39.grit.shared.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.core.settings.Sections
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.habit.HabitsAction
import com.shub39.grit.shared.ui.habit.ui.sections.AnalyticsPage
import com.shub39.grit.shared.ui.habit.ui.sections.Calendar
import com.shub39.grit.shared.ui.habit.ui.sections.CalendarHeatMap
import com.shub39.grit.shared.ui.habit.ui.sections.HabitsList
import com.shub39.grit.shared.ui.habit.ui.sections.OverallAnalytics
import com.shub39.grit.shared.ui.setting.SettingsAction
import com.shub39.grit.shared.ui.setting.ui.section.About
import com.shub39.grit.shared.ui.setting.ui.section.BackupPage
import com.shub39.grit.shared.ui.setting.ui.section.Changelog
import com.shub39.grit.shared.ui.setting.ui.section.LookAndFeelPage
import com.shub39.grit.shared.ui.setting.ui.section.RootPage
import com.shub39.grit.shared.ui.task.ui.TasksPage
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.viewmodel.HabitViewModel
import com.shub39.grit.shared.ui.viewmodel.MainViewModel
import com.shub39.grit.shared.ui.viewmodel.SettingsViewModel
import com.shub39.grit.shared.ui.viewmodel.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GritNavDisplay(
    modifier: Modifier = Modifier,
    paywall: @Composable (Boolean, () -> Unit) -> Unit,
) {
    val globalVM = koinViewModel<MainViewModel>()
    val globalState by globalVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2()
    val topLevelBackStack = retain {
        TopLevelBackStack(
            startKey =
                when (globalState.startingSection) {
                    Sections.Tasks -> TaskList
                    Sections.Habits -> HabitsList
                }
        )
    }

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass.windowSizeClass) {
        GritTheme(theme = globalState.theme) {
            SharedTransitionLayout(
                modifier = modifier.background(MaterialTheme.colorScheme.background)
            ) {
                val responsiveNavigationSceneDecoratorStrategy =
                    rememberResponsiveNavigationSceneDecoratorStrategy<Routes>(
                        navBar = {
                            NavBar(
                                currentRoute = topLevelBackStack.backStack.lastOrNull(),
                                onNavigate = { topLevelBackStack.add(it) },
                            )
                        },
                        navRail = {
                            NavRail(
                                currentRoute = topLevelBackStack.backStack.lastOrNull(),
                                onNavigate = { topLevelBackStack.add(it) },
                            )
                        },
                        sharedTransitionScope = this,
                    )

                NavDisplay(
                    backStack = topLevelBackStack.backStack,
                    sharedTransitionScope = this,
                    sceneDecoratorStrategies = listOf(responsiveNavigationSceneDecoratorStrategy),
                    onBack = { topLevelBackStack.removeLast() },
                    entryProvider =
                        entryProvider {
                            entry<TaskList>(metadata = fadeTransitionMetadata()) {
                                val viewModel = koinViewModel<TasksViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                TasksPage(state = state, onAction = viewModel::onAction)
                            }

                            entry<HabitsList>(metadata = fadeTransitionMetadata()) {
                                val viewModel = koinViewModel<HabitViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                HabitsList(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateToAnalytics = {
                                        topLevelBackStack.add(HabitAnalytics)
                                    },
                                    onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
                                    onNavigateToOverallAnalytics = {
                                        topLevelBackStack.add(OverallAnalytics)
                                    },
                                    isUserSubscribed = globalState.isUserSubscribed,
                                )
                            }

                            entry<HabitAnalytics>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<HabitViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                AnalyticsPage(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                    onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
                                    onNavigateToCalendar = { topLevelBackStack.add(Calendar) },
                                    isUserSubscribed = globalState.isUserSubscribed,
                                )
                            }

                            entry<Calendar>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<HabitViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                Calendar(
                                    state = state,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                    onDateClick = { habit, date ->
                                        viewModel.onAction(HabitsAction.InsertStatus(habit, date))
                                    },
                                )
                            }

                            entry<OverallAnalytics>(metadata = verticalTransitionMetadata()) {
                                val viewModel = koinViewModel<HabitViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                OverallAnalytics(
                                    state = state,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                    onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
                                    isUserSubscribed = globalState.isUserSubscribed,
                                    onAction = viewModel::onAction,
                                    onNavigateToCalendarHeatMap = {
                                        topLevelBackStack.add(CalendarHeatMap)
                                    },
                                )
                            }

                            entry<CalendarHeatMap>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<HabitViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(Unit) {
                                    viewModel.onAction(HabitsAction.OnOverallAnalyticsViewed)
                                }

                                CalendarHeatMap(
                                    state = state,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                    onChangeSelectedDay = {
                                        viewModel.onAction(
                                            HabitsAction.FetchCompletedHabitsForDate(it)
                                        )
                                    },
                                )
                            }

                            entry<Paywall>(metadata = verticalTransitionMetadata()) {
                                paywall(globalState.isUserSubscribed) {
                                    globalVM.updateSubscription()
                                }
                            }

                            entry<SettingsHome>(metadata = fadeTransitionMetadata()) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                RootPage(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateToLookAndFeel = {
                                        topLevelBackStack.add(LookAndFeel)
                                    },
                                    onNavigateToBackup = { topLevelBackStack.add(Backup) },
                                    onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
                                    onNavigateToChangelog = { topLevelBackStack.add(Changelog) },
                                    onNavigateToAppInfo = { topLevelBackStack.add(About) },
                                )
                            }

                            entry<LookAndFeel>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LookAndFeelPage(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    isUserSubscribed = globalState.isUserSubscribed,
                                    onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                )
                            }

                            entry<Backup>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                BackupPage(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                )
                            }

                            entry<About>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(Unit) {
                                    viewModel.onAction(SettingsAction.OnAboutViewed)
                                }

                                About(
                                    versionName = state.currentVersion ?: "1.0.00-Demo",
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                )
                            }

                            entry<Changelog>(metadata = horizontalTransitionMetadata()) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(Unit) {
                                    viewModel.onAction(SettingsAction.OnChangelogViewed)
                                }

                                Changelog(
                                    changelog = state.changelog,
                                    onNavigateBack = { topLevelBackStack.removeLast() },
                                )
                            }
                        },
                )
            }
        }
    }
}
