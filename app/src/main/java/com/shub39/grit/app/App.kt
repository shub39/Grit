package com.shub39.grit.app

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
import com.shub39.grit.app.AppSections.Companion.toIconRes
import com.shub39.grit.app.AppSections.Companion.toStringRes
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.core.domain.MainAppState
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
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
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
private sealed interface GlobalRoutes {
    @Serializable
    data object PaywallPage : GlobalRoutes
    
    @Serializable
    data object App : GlobalRoutes
}

@Serializable
private sealed interface AppSections {
    @Serializable
    data object HabitsPages : AppSections

    @Serializable
    data object TaskPages : AppSections

    @Serializable
    data object SettingsPages : AppSections

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
fun App(
    state: MainAppState
) {
    val mainNavController = rememberNavController()
    
    NavHost(
        navController = mainNavController,
        startDestination = GlobalRoutes.App
    ) {
        composable<GlobalRoutes.PaywallPage> {
            PaywallPage(
                isPlusUser = state.isUserSubscribed,
                onDismissRequest = { mainNavController.navigateUp() }
            ) 
        }
        
        composable<GlobalRoutes.App> {
            val windowSizeClass = LocalWindowSizeClass.current
            
            var currentRoute: AppSections by remember { mutableStateOf(AppSections.TaskPages) }
            val navController = rememberNavController()

            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    Scaffold(
                        bottomBar = {
                            AppNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        popUpTo(
                                            route = when (state.startingSection) {
                                                Sections.Habits -> AppSections.HabitsPages
                                                Sections.Tasks -> AppSections.TaskPages
                                            },
                                            popUpToBuilder = { saveState = true }
                                        )
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = when (state.startingSection) {
                                Sections.Tasks -> AppSections.TaskPages
                                Sections.Habits -> AppSections.HabitsPages
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
                            composable<AppSections.TaskPages> {
                                currentRoute = AppSections.TaskPages
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(
                                    state = taskPageState,
                                    onAction = tvm::onAction
                                )
                            }

                            composable<AppSections.SettingsPages> {
                                currentRoute = AppSections.SettingsPages
                                val svm: SettingsViewModel = koinInject()
                                val settingsState by svm.state.collectAsStateWithLifecycle()

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                    onNavigateToPaywall = {
                                        mainNavController.navigate(GlobalRoutes.PaywallPage)
                                    }
                                )
                            }

                            composable<AppSections.HabitsPages> {
                                currentRoute = AppSections.HabitsPages
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                    onNavigateToPaywall = {
                                        mainNavController.navigate(GlobalRoutes.PaywallPage)
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        AppNavRail(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    popUpTo(
                                        route = when (state.startingSection) {
                                            Sections.Habits -> AppSections.HabitsPages
                                            Sections.Tasks -> AppSections.TaskPages
                                        },
                                        popUpToBuilder = { saveState = true }
                                    )
                                    restoreState = true
                                }
                            }
                        )

                        NavHost(
                            navController = navController,
                            startDestination = when (state.startingSection) {
                                Sections.Tasks -> AppSections.TaskPages
                                Sections.Habits -> AppSections.HabitsPages
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                            popExitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            composable<AppSections.TaskPages> {
                                currentRoute = AppSections.TaskPages
                                val tvm: TasksViewModel = koinViewModel()
                                val taskPageState by tvm.state.collectAsStateWithLifecycle()

                                TasksPage(
                                    state = taskPageState,
                                    onAction = tvm::onAction
                                )
                            }

                            composable<AppSections.SettingsPages> {
                                currentRoute = AppSections.SettingsPages
                                val svm: SettingsViewModel = koinInject()
                                val settingsState by svm.state.collectAsStateWithLifecycle()

                                SettingsGraph(
                                    state = settingsState,
                                    onAction = svm::onAction,
                                    onNavigateToPaywall = {
                                        mainNavController.navigate(GlobalRoutes.PaywallPage)
                                    }
                                )
                            }

                            composable<AppSections.HabitsPages> {
                                currentRoute = AppSections.HabitsPages
                                val hvm: HabitViewModel = koinViewModel()
                                val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                                HabitsGraph(
                                    state = habitsPageState,
                                    onAction = hvm::onAction,
                                    onNavigateToPaywall = {
                                        mainNavController.navigate(GlobalRoutes.PaywallPage)
                                    }
                                )
                            }
                        }
                    }
                }
            } 
        }
    }
}

@Composable
private fun AppNavRail(
    currentRoute: AppSections,
    onNavigate: (AppSections) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
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

@Composable
private fun AppNavBar(
    currentRoute: AppSections,
    onNavigate: (AppSections) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        AppSections.mainRoutes.forEach { route ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
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