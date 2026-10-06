package com.shub39.grit.shared.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.habit.ui.sections.HabitsList
import com.shub39.grit.shared.ui.task.ui.TasksPage
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.viewmodel.HabitViewModel
import com.shub39.grit.shared.ui.viewmodel.MainViewModel
import com.shub39.grit.shared.ui.viewmodel.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GritNavDisplay(
    modifier: Modifier = Modifier,
    paywall: @Composable (Boolean, () -> Unit) -> Unit
) {
    val globalVM = koinViewModel<MainViewModel>()
    val globalState by globalVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2()
    val topLevelBackStack = retain {
        TopLevelBackStack(
            startKey = when (globalState.startingSection) {
                Tasks -> Routes.TaskPages.TaskList
                Habits -> Routes.HabitPages.HabitList
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
                        navBar = {},
                        navRail = {},
                        sharedTransitionScope = this
                    )

                NavDisplay(
                    backStack = topLevelBackStack.backStack,
                    sharedTransitionScope = this,
                    sceneDecoratorStrategies = listOf(responsiveNavigationSceneDecoratorStrategy),
                    onBack = { topLevelBackStack.removeLast() },
                    entryProvider = entryProvider {
                        entry<Routes.TaskPages.TaskList>(metadata = fadeTransitionMetadata()) {
                            val viewModel = koinViewModel<TasksViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            TasksPage(
                                state = state,
                                onAction = viewModel::onAction
                            )
                        }

                        entry<Routes.HabitPages.HabitList>(metadata = fadeTransitionMetadata()) {
                            val viewModel = koinViewModel<HabitViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            HabitsList(
                                state = state,
                                onAction = viewModel::onAction,
                                lazyListState = rememberLazyListState(),
                                onNavigateToAnalytics = {
                                    topLevelBackStack.add(Routes.HabitPages.HabitAnalytics)
                                }
                            )
                        }

                        entry<Routes.HabitPages.HabitAnalytics> {

                        }
                    }
                )
            }
        }
    }
}