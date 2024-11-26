package com.shub39.grit.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.core.presentation.SettingsPage
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
    var currentRoute: BottomAppBarDestination by remember { mutableStateOf(BottomAppBarDestination.TasksPage) }

    val taskPageState by tvm.tasksState.collectAsStateWithLifecycle()
    val habitsPageState by hvm.habitsPageState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = currentRoute,
                onChange = { currentRoute = it }
            )
        }
    ) { innerPadding ->

      AnimatedContent(
          targetState = currentRoute,
          modifier = Modifier.padding(innerPadding),
          label = "page"
      ) {
          when (it) {
              BottomAppBarDestination.TasksPage -> {
                  TaskPage(
                      state = taskPageState,
                      action = tvm::taskPageAction
                  )
              }
              BottomAppBarDestination.HabitsPage -> {
                  HabitsPage(
                      state = habitsPageState,
                      action = hvm::habitsPageAction
                  )
              }
              BottomAppBarDestination.SettingsPage -> {
                  SettingsPage(tvm)
              }
          }
      }
    }
}