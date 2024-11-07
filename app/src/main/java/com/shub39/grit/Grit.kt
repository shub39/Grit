package com.shub39.grit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.ui.component.BottomBar
import com.shub39.grit.ui.page.SettingsPage
import com.shub39.grit.ui.page.habits_page.HabitsPage
import com.shub39.grit.ui.page.task_page.TaskPage
import com.shub39.grit.ui.page.habits_page.HabitViewModel
import com.shub39.grit.ui.page.task_page.TaskListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

        KoinContext {
            NavHost (
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
}

object TasksPage {
    const val ROUTE = "tasks"
}

object HabitsPage {
    const val ROUTE = "habits"
}

object SettingsPage {
    const val ROUTE = "settings"
}