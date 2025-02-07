package com.shub39.grit.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.R
import com.shub39.grit.core.presentation.settings.AboutPage
import com.shub39.grit.core.presentation.settings.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.SettingsPage
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.presentation.HabitViewModel
import com.shub39.grit.habits.presentation.HabitsPage
import com.shub39.grit.tasks.presentation.EditCategories
import com.shub39.grit.tasks.presentation.TaskListViewModel
import com.shub39.grit.tasks.presentation.task_page.TaskPage
import org.koin.androidx.compose.koinViewModel

@Composable
fun Grit(
    tvm: TaskListViewModel = koinViewModel(),
    hvm: HabitViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    var currentRoute: Routes by remember { mutableStateOf(Routes.TasksPage) }

    val taskPageState by tvm.tasksState.collectAsStateWithLifecycle()
    val habitsPageState by hvm.habitsPageState.collectAsStateWithLifecycle()

    val navigator = { route: Routes ->
        if (currentRoute != route) {
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo(Routes.TasksPage) { saveState = true }
                restoreState = true
            }
        }
    }

    GritTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    listOf(
                        Routes.TasksPage,
                        Routes.HabitsPage,
                        Routes.SettingsGraph
                    ).forEach { route ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navigator(route)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        when (route) {
                                            Routes.HabitsPage -> R.drawable.round_alarm_24
                                            Routes.TasksPage -> R.drawable.round_checklist_24
                                            else -> R.drawable.round_settings_24
                                        }
                                    ),
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        when (route) {
                                            Routes.HabitsPage -> R.string.habits
                                            Routes.TasksPage -> R.string.tasks
                                            else -> R.string.settings
                                        }
                                    )
                                )
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.TasksPage,
                modifier = Modifier
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable<Routes.TasksPage> {
                    currentRoute = Routes.TasksPage

                    TaskPage(
                        state = taskPageState,
                        action = tvm::taskPageAction
                    )
                }

                navigation<Routes.SettingsGraph>(
                    startDestination = Routes.Settings
                ) {
                    composable<Routes.Settings> {
                        currentRoute = Routes.SettingsGraph

                        SettingsPage(
                            onCategoryClick = { navController.navigate(Routes.Categories) },
                            onAboutClick = { navController.navigate(Routes.About) },
                            onLookAndFeelClick = { navController.navigate(Routes.LookAndFeel) }
                        )
                    }

                    composable<Routes.About> {
                        currentRoute = Routes.SettingsGraph

                        AboutPage()
                    }

                    composable<Routes.Categories> {
                        currentRoute = Routes.SettingsGraph

                        EditCategories(
                            state = taskPageState,
                            action = tvm::taskPageAction
                        )
                    }

                    composable<Routes.LookAndFeel> {
                        currentRoute = Routes.SettingsGraph

                        LookAndFeelPage()
                    }
                }

                composable<Routes.HabitsPage> {
                    currentRoute = Routes.HabitsPage

                    HabitsPage(
                        state = habitsPageState,
                        action = hvm::habitsPageAction
                    )
                }
            }
        }
    }

}