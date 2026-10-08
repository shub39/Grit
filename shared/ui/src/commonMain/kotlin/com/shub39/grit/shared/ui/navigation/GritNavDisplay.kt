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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.core.settings.Sections
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.app.MainAppState
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

    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val topLevelBackStack = retain(globalState.startingSection) {
        TopLevelBackStack(
            startKey =
                when (globalState.startingSection) {
                    Sections.Tasks -> TaskList
                    Sections.Habits -> HabitsList
                }
        )
    }

    CompositionLocalProvider(LocalWindowSizeClass provides adaptiveInfo.windowSizeClass) {
        GritTheme(theme = globalState.theme) {
            SharedTransitionLayout(modifier = modifier.background(colorScheme.background)) {
                val windowSizeClass = LocalWindowSizeClass.current

                val listDetailSceneStrategy =
                    remember(windowSizeClass) { ListDetailSceneStrategy<Routes>(windowSizeClass) }
                val twoPaneSceneStrategy =
                    remember(windowSizeClass) { TwoPaneSceneStrategy<Routes>(windowSizeClass) }
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
                    sceneStrategies = listOf(twoPaneSceneStrategy, listDetailSceneStrategy),
                    onBack = { topLevelBackStack.removeLast() },
                    entryProvider =
                        entryProvider {
                            entry<TaskList>(metadata = fadeTransitionMetadata()) {
                                val viewModel = koinViewModel<TasksViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                TasksPage(state = state, onAction = viewModel::onAction)
                            }

                            habitsScreens(
                                topLevelBackStack = topLevelBackStack,
                                globalState = globalState,
                            )

                            entry<Paywall>(metadata = verticalTransitionMetadata()) {
                                LaunchedEffect(Unit) { globalVM.trackPaywallOpened() }
                                paywall(globalState.isUserSubscribed) {
                                    globalVM.updateSubscription()
                                    topLevelBackStack.removeLast()
                                }
                            }

                            settingsScreens(
                                topLevelBackStack = topLevelBackStack,
                                globalState = globalState,
                            )
                        },
                )
            }
        }
    }
}

private fun EntryProviderScope<Routes>.habitsScreens(
    topLevelBackStack: TopLevelBackStack,
    globalState: MainAppState,
) {
    entry<HabitsList>(metadata = ListDetailScene.listPane() + fadeTransitionMetadata()) {
        val viewModel = koinViewModel<HabitViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.onAction(OnHabitsOpened)
        }

        HabitsList(
            state = state,
            onAction = viewModel::onAction,
            onNavigateToAnalytics = { topLevelBackStack.add(HabitAnalytics) },
            onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
            onNavigateToOverallAnalytics = { topLevelBackStack.add(OverallAnalytics) },
            isUserSubscribed = globalState.isUserSubscribed,
            modifier = Modifier.background(colorScheme.background),
        )
    }

    entry<HabitAnalytics>(
        metadata =
            TwoPaneScene.twoPane() + ListDetailScene.detailPane() + horizontalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<HabitViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        AnalyticsPage(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { topLevelBackStack.removeLast() },
            onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
            onNavigateToCalendar = { topLevelBackStack.add(Calendar) },
            isUserSubscribed = globalState.isUserSubscribed,
            modifier = Modifier.background(colorScheme.background),
        )
    }

    entry<Calendar>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<HabitViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Calendar(
            state = state,
            onNavigateBack = { topLevelBackStack.removeLast() },
            onDateClick = { habit, date ->
                viewModel.onAction(HabitsAction.InsertStatus(habit, date))
            },
            modifier = Modifier.background(colorScheme.background),
        )
    }

    entry<OverallAnalytics>(
        metadata =
            TwoPaneScene.twoPane() + ListDetailScene.detailPane() + horizontalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<HabitViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnOverallAnalyticsViewed) }

        OverallAnalytics(
            state = state,
            onNavigateBack = { topLevelBackStack.removeLast() },
            onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
            isUserSubscribed = globalState.isUserSubscribed,
            onAction = viewModel::onAction,
            onNavigateToCalendarHeatMap = { topLevelBackStack.add(CalendarHeatMap) },
            modifier = Modifier.background(colorScheme.background),
        )
    }

    entry<CalendarHeatMap>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<HabitViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        CalendarHeatMap(
            state = state,
            onNavigateBack = { topLevelBackStack.removeLast() },
            onChangeSelectedDay = {
                viewModel.onAction(HabitsAction.FetchCompletedHabitsForDate(it))
            },
            modifier = Modifier.background(colorScheme.background),
        )
    }
}

private fun EntryProviderScope<Routes>.settingsScreens(
    topLevelBackStack: TopLevelBackStack,
    globalState: MainAppState,
) {
    entry<SettingsHome>(metadata = TwoPaneScene.twoPane() + fadeTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.onAction(OnSettingsOpened)
        }

        RootPage(
            modifier = Modifier.background(colorScheme.background),
            state = state,
            onAction = viewModel::onAction,
            onNavigateToLookAndFeel = { topLevelBackStack.add(LookAndFeel) },
            onNavigateToBackup = { topLevelBackStack.add(Backup) },
            onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
            onNavigateToChangelog = { topLevelBackStack.add(Changelog) },
            onNavigateToAppInfo = { topLevelBackStack.add(About) },
        )
    }

    entry<LookAndFeel>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LookAndFeelPage(
            state = state,
            onAction = viewModel::onAction,
            isUserSubscribed = globalState.isUserSubscribed,
            onNavigateToPaywall = { topLevelBackStack.add(Paywall) },
            onNavigateBack = { topLevelBackStack.removeLast() },
            modifier = Modifier.background(colorScheme.background),
        )
    }

    entry<Backup>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        BackupPage(
            modifier = Modifier.background(colorScheme.background),
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { topLevelBackStack.removeLast() },
        )
    }

    entry<About>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnAboutViewed) }

        About(
            modifier = Modifier.background(colorScheme.background),
            versionName = state.currentVersion ?: "1.0.00-Demo",
            onNavigateBack = { topLevelBackStack.removeLast() },
        )
    }

    entry<Changelog>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnChangelogViewed) }

        Changelog(
            modifier = Modifier.background(colorScheme.background),
            changelog = state.changelog,
            onNavigateBack = { topLevelBackStack.removeLast() },
        )
    }
}
