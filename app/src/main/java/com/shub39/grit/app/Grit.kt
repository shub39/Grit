package com.shub39.grit.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.habits.presentation.HabitViewModel
import com.shub39.grit.habits.presentation.HabitsPage
import com.shub39.grit.tasks.presentation.TaskListViewModel
import com.shub39.grit.tasks.presentation.TaskPage
import org.koin.androidx.compose.koinViewModel

@Composable
fun Grit(
    tvm: TaskListViewModel = koinViewModel(),
    hvm: HabitViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    var currentRoute: Routes by remember { mutableStateOf(Routes.TasksGraph) }

    val taskPageState by tvm.tasksState.collectAsStateWithLifecycle()
    val habitsPageState by hvm.habitsPageState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = currentRoute,
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
                    currentRoute = Routes.TasksGraph

                    TaskPage(
                        state = taskPageState,
                        action = tvm::taskPageAction
                    )
                }

                composable<Routes.TasksSettings> {
                    currentRoute = Routes.TasksSettings


                }
            }

            navigation<Routes.HabitGraph>(
                startDestination = Routes.HabitsPage
            ) {
                composable<Routes.HabitsPage> {
                    currentRoute = Routes.HabitsPage

                    HabitsPage(
                        state = habitsPageState,
                        action = hvm::habitsPageAction
                    )
                }

                composable<Routes.HabitsSettings> {
                    currentRoute = Routes.HabitsSettings


                }
            }
        }
    }

}