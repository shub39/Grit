import Routes.Companion.toDisplayText
import Routes.Companion.toImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksPage
import com.shub39.grit.core.utils.LocalWindowSizeClass
import kotlinx.serialization.Serializable

@Serializable
private sealed interface Routes {
    @Serializable
    data object Tasks : Routes

    @Serializable
    data object Habits : Routes

    companion object {
        val allRoutes = listOf<Routes>(
            Tasks, Habits
        )

        fun Routes.toImageVector(): ImageVector {
            return when (this) {
                Tasks -> Icons.Rounded.Checklist
                Habits -> Icons.Rounded.Loop
            }
        }

        fun Routes.toDisplayText(): String {
            return when (this) {
                Tasks -> "Tasks"
                Habits -> "Habits"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App(
    stateProvider: StateProvider,
    isDark: Boolean,
    onSwitchTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var currentRoute: Routes by remember { mutableStateOf(Routes.Tasks) }

    val windowSizeClass = LocalWindowSizeClass.current

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        Routes.allRoutes.forEach { route: Routes ->
                            NavigationBarItem(
                                selected = currentRoute == route,
                                onClick = {
                                    currentRoute = route
                                    navController.navigate(currentRoute)
                                },
                                icon = {
                                    Icon(
                                        imageVector = route.toImageVector(),
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(
                                        text = route.toDisplayText()
                                    )
                                },
                                alwaysShowLabel = true
                            )
                        }
                    }
                }
            ) {
                AppContent(
                    navController = navController,
                    stateProvider = stateProvider,
                    modifier = Modifier.padding(it)
                )
            }
        }

        else -> {
            Row(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                NavigationRail(
                    header = {
                        FloatingActionButton(
                            onClick = { onSwitchTheme(!isDark) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                imageVector = if (isDark) {
                                    Icons.Rounded.LightMode
                                } else {
                                    Icons.Rounded.DarkMode
                                },
                                contentDescription = null
                            )
                        }

                        FloatingActionButton(
                            onClick = { stateProvider.onRefresh() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.clip(
                        RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
                    )
                ) {
                    Routes.allRoutes.forEach { route: Routes ->
                        NavigationRailItem(
                            selected = currentRoute == route,
                            onClick = {
                                currentRoute = route
                                navController.navigate(currentRoute)
                            },
                            icon = {
                                Icon(
                                    imageVector = route.toImageVector(),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = route.toDisplayText()
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                }

                AppContent(
                    navController = navController,
                    stateProvider = stateProvider,
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    navController: NavHostController,
    stateProvider: StateProvider,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Tasks,
        modifier = modifier
    ) {
        composable<Routes.Tasks> {
            val state by stateProvider.taskState.collectAsState()

            TasksPage(
                state = state,
                onAction = stateProvider::onTaskAction
            )
        }

        composable<Routes.Habits> {
            val state by stateProvider.habitState.collectAsState()

            HabitsGraph(
                state = state,
                onAction = stateProvider::onHabitAction
            )
        }
    }
}