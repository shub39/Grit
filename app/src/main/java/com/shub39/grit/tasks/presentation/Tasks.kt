package com.shub39.grit.tasks.presentation

import android.content.res.Configuration
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.presentation.component.EditCategories
import com.shub39.grit.tasks.presentation.component.TaskList

@Composable
fun Tasks(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit
) = PageFill {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TasksRoutes.TasksList,
        enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
        exitTransition = { fadeOut() },
        popEnterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
        popExitTransition = { fadeOut() }
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

@Preview(
    showSystemUi = true, showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun Preview() {
    val data = (0L..3L).associate { category ->
        Category(
            id = category,
            name = "Category: $category",
            color = "gray"
        ) to (0L..100L).map {
            Task(
                id = it,
                categoryId = category,
                title = "$category $it",
                status = it % 2L == 0L
            )
        }
    }
    var state by remember {
        mutableStateOf(
            TaskPageState(
                currentCategory = data.keys.first(),
                tasks = data
            )
        )
    }

    GritTheme {
        Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                Tasks(
                    state = state,
                    onAction = {
                        when (it) {
                            is TaskPageAction.ChangeCategory -> {
                                state = state.copy(currentCategory = it.category)
                            }
                            else -> {}
                        }
                    },
                )
            }
        }
    }
}