package com.shub39.grit.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.R
import com.shub39.grit.habits.presentation.HabitViewModel
import com.shub39.grit.habits.presentation.HabitsPage
import com.shub39.grit.tasks.presentation.TaskListViewModel
import com.shub39.grit.tasks.presentation.task_page.TaskPage
import com.shub39.grit.tasks.presentation.tasks_settings.TaskSettings
import org.koin.androidx.compose.koinViewModel

@Composable
fun Grit(
    tvm: TaskListViewModel = koinViewModel(),
    hvm: HabitViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    var currentRoute: Routes by remember { mutableStateOf(Routes.TasksGraph) }

    val taskPageState by tvm.tasksState.collectAsStateWithLifecycle()
    val taskSettingsState by tvm.tasksSettings.collectAsStateWithLifecycle()
    val habitsPageState by hvm.habitsPageState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            AnimatedContent(
                targetState = currentRoute,
                label = "fab"
            ) {
                when (it) {
                    Routes.HabitsPage -> {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(Routes.TasksGraph) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_checklist_24),
                                contentDescription = null
                            )
                        }
                    }
                    Routes.TasksPage -> {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(Routes.HabitGraph) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_alarm_24),
                                contentDescription = null
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TasksGraph,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            navigation<Routes.TasksGraph>(
                startDestination = Routes.TasksPage
            ) {
                composable<Routes.TasksPage> {
                    currentRoute = Routes.TasksPage

                    TaskPage(
                        state = taskPageState,
                        action = tvm::taskPageAction,
                        onSettingsClick = {
                            navController.navigate(Routes.TasksSettings) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable<Routes.TasksSettings> {
                    currentRoute = Routes.TasksSettings

                    TaskSettings(
                        state = taskSettingsState,
                        action = tvm::tasksSettingsAction
                    )
                }
            }

            navigation<Routes.HabitGraph>(
                startDestination = Routes.HabitsPage
            ) {
                composable<Routes.HabitsPage> {
                    currentRoute = Routes.HabitsPage

                    HabitsPage(
                        state = habitsPageState,
                        action = hvm::habitsPageAction,
                        onSettingsClick = {
                            navController.navigate(Routes.HabitsSettings) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable<Routes.HabitsSettings> {
                    currentRoute = Routes.HabitsSettings


                }
            }
        }
    }

}