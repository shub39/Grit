package com.shub39.grit.core.tasks.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.tasks.presentation.ui.section.EditCategories
import com.shub39.grit.core.tasks.presentation.ui.section.TaskList
import kotlinx.serialization.Serializable

@Serializable
private sealed interface TasksRoutes {
    @Serializable
    data object TasksList: TasksRoutes

    @Serializable
    data object EditCategories: TasksRoutes
}

@Composable
fun TasksGraph(
    state: TaskState,
    onAction: (TaskAction) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TasksRoutes.TasksList,
        enterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        composable<TasksRoutes.TasksList> {
            TaskList(
                state = state,
                onAction = onAction,
                onNavigateToEditCategories = {
                    navController.navigate(TasksRoutes.EditCategories) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<TasksRoutes.EditCategories> {
            EditCategories(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}