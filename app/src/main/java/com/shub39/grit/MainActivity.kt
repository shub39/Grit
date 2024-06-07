package com.shub39.grit

import androidx.compose.material3.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.component.BottomAppBarDestination
import com.shub39.grit.page.AnalyticsPage
import com.shub39.grit.page.TodoPage
import com.shub39.grit.page.HabitsPage
import com.shub39.grit.ui.theme.GritTheme
import com.shub39.grit.viewModel.HabitViewModel
import com.shub39.grit.viewModel.TaskListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskListViewModel = TaskListViewModel(applicationContext)
        val habitsViewModel = HabitViewModel(applicationContext)

        enableEdgeToEdge()
        setContent {
            GritTheme {
                val navController = rememberNavController()
                val snackbarHost = remember { SnackbarHostState() }
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    snackbarHost = { SnackbarHost(snackbarHost) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomAppBarDestination.TodoPage.direction,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomAppBarDestination.TodoPage.direction) {
                            TodoPage(taskListViewModel)
                        }
                        composable(BottomAppBarDestination.HabitsPage.direction) {
                            HabitsPage(habitsViewModel)
                        }
                        composable(BottomAppBarDestination.AnalyticsPage.direction) {
                            AnalyticsPage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavController) {
    NavigationBar(tonalElevation = 8.dp) {
        BottomAppBarDestination.entries.forEach { destination ->
            val isSelected =
                navController.currentBackStackEntryAsState().value?.destination?.route == destination.direction
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.direction)
                },
                icon = {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(id = destination.iconSelected),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = destination.iconSelected),
                            contentDescription = null
                        )
                    }
                },
                label = { Text(stringResource(id = destination.label)) },
                alwaysShowLabel = false
            )
        }
    }
}