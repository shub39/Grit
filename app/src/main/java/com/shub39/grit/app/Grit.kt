package com.shub39.grit.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.app.Routes.Companion.toIconRes
import com.shub39.grit.app.Routes.Companion.toStringRes
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsGraph
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
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Serializable
private sealed interface Routes {
    @Serializable
    data object HabitsPages : Routes

    @Serializable
    data object TaskPages : Routes

    @Serializable
    data object SettingsPages : Routes

    companion object {
        val mainRoutes = listOf(TaskPages, HabitsPages, SettingsPages)

        fun Routes.toStringRes(): StringResource {
            return when (this) {
                HabitsPages -> Res.string.habits
                TaskPages -> Res.string.tasks
                SettingsPages -> Res.string.settings
            }
        }

        fun Routes.toIconRes(): DrawableResource {
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
fun Grit() {
    val windowSizeClass = LocalWindowSizeClass.current

    val svm: SettingsViewModel = koinInject()
    val settingsState by svm.state.collectAsStateWithLifecycle()

    var currentRoute: Routes by remember { mutableStateOf(Routes.TaskPages) }
    val navController = rememberNavController()

    AnimatedContent(
        targetState = settingsState.showPaywall,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { showPaywall ->
        if (!showPaywall) {
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                Routes.mainRoutes.forEach { route ->
                                    NavigationBarItem(
                                        selected = currentRoute == route,
                                        onClick = {
                                            if (currentRoute != route) {
                                                navController.navigate(route) {
                                                    launchSingleTop = true
                                                    popUpTo(
                                                        route = when (settingsState.startingPage) {
                                                            Pages.Habits -> Routes.HabitsPages
                                                            Pages.Tasks -> Routes.TaskPages
                                                        },
                                                        popUpToBuilder = { saveState = true }
                                                    )
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(route.toIconRes()),
                                                contentDescription = null,
                                            )
                                        },
                                        label = { Text(text = stringResource(route.toStringRes())) },
                                        alwaysShowLabel = false
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = when (settingsState.startingPage) {
                                Pages.Tasks -> Routes.TaskPages
                                Pages.Habits -> Routes.HabitsPages
                            },
                            modifier = Modifier
                                .padding(
                                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                                    bottom = padding.calculateBottomPadding()
                                )
                                .background(MaterialTheme.colorScheme.background),
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                            popExitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            composable<Routes.TaskPages> {
                                currentRoute = Routes.TaskPages
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(
                                    state = taskPageState,
                                    onAction = tvm::onAction
                                )
                            }

                            composable<Routes.SettingsPages> {
                                currentRoute = Routes.SettingsPages

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                )
                            }

                            composable<Routes.HabitsPages> {
                                currentRoute = Routes.HabitsPages
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                )
                            }
                        }
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Routes.mainRoutes.forEach { route ->
                                NavigationRailItem(
                                    selected = currentRoute == route,
                                    onClick = {
                                        if (currentRoute != route) {
                                            navController.navigate(route) {
                                                launchSingleTop = true
                                                popUpTo(
                                                    route = when (settingsState.startingPage) {
                                                        Pages.Habits -> Routes.HabitsPages
                                                        Pages.Tasks -> Routes.TaskPages
                                                    },
                                                    popUpToBuilder = { saveState = true }
                                                )
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(route.toIconRes()),
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(text = stringResource(route.toStringRes())) },
                                    alwaysShowLabel = false
                                )
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = when (settingsState.startingPage) {
                                Pages.Tasks -> Routes.TaskPages
                                Pages.Habits -> Routes.HabitsPages
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                            popExitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            composable<Routes.TaskPages> {
                                currentRoute = Routes.TaskPages
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(
                                    state = taskPageState,
                                    onAction = tvm::onAction
                                )
                            }

                            composable<Routes.SettingsPages> {
                                currentRoute = Routes.SettingsPages

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                )
                            }

                            composable<Routes.HabitsPages> {
                                currentRoute = Routes.HabitsPages
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            PaywallPage(
                isPlusUser = settingsState.isUserSubscribed,
                onDismissRequest = { svm.onAction(SettingsAction.OnPaywallDismiss) }
            )
        }
    }
}