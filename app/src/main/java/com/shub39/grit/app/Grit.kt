package com.shub39.grit.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.presentation.SettingsPage
import com.shub39.grit.habits.presentation.HabitsPage
import com.shub39.grit.tasks.presentation.TaskPage
import com.shub39.grit.habits.presentation.HabitViewModel
import com.shub39.grit.tasks.presentation.TaskListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Grit(
    tvm: TaskListViewModel = koinViewModel(),
    hvm: HabitViewModel = koinViewModel()
) {
    val navController = rememberNavController()

    val taskPageState by tvm.tasksState.collectAsStateWithLifecycle()
    val habitsPageState by hvm.habitsPageState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = TasksPage.ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TasksPage.ROUTE) {
                TaskPage(
                    state = taskPageState,
                    action = tvm::taskPageAction
                )
            }

            composable(HabitsPage.ROUTE) {
                HabitsPage(
                    state = habitsPageState,
                    action = hvm::habitsPageAction
                )
            }

            composable(SettingsPage.ROUTE) {
                SettingsPage(tvm)
            }
        }
    }
}


// The main pages
object TasksPage {
    const val ROUTE = "tasks"
}

object HabitsPage {
    const val ROUTE = "habits"
}

object SettingsPage {
    const val ROUTE = "settings"
}