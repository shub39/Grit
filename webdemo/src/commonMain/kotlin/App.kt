import Routes.Companion.toDisplayText
import Routes.Companion.toDrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksPage
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.alarm
import grit.shared.core.generated.resources.check_list
import grit.shared.core.generated.resources.dark_mode
import grit.shared.core.generated.resources.light_mode
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

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

        fun Routes.toDrawableRes(): DrawableResource {
            return when (this) {
                Tasks -> Res.drawable.check_list
                Habits -> Res.drawable.alarm
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

@Composable
fun App() {
    val navController = rememberNavController()
    var isDark by remember { mutableStateOf(true) }
    var currentRoute: Routes by remember { mutableStateOf(Routes.Tasks) }
    val windowSizeClass = LocalWindowSizeClass.current

    DynamicMaterialTheme(
        seedColor = Color(0xFFBA2C),
        isDark = isDark
    ) {
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
                                            imageVector = vectorResource(route.toDrawableRes()),
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
                                onClick = { isDark = !isDark },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Icon(
                                    imageVector = vectorResource(
                                        if (isDark) {
                                            Res.drawable.light_mode
                                        } else {
                                            Res.drawable.dark_mode
                                        }
                                    ),
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
                                        imageVector = vectorResource(route.toDrawableRes()),
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

                    AppContent(navController = navController)
                }
            }
        }
    }
}


@Composable
private fun AppContent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Tasks,
        modifier = modifier
    ) {
        composable<Routes.Tasks> {
            val state by DummyStateProvider.taskState.collectAsState()

            TasksPage(
                state = state,
                onAction = DummyStateProvider::onTaskAction
            )
        }

        composable<Routes.Habits> {
            val state by DummyStateProvider.habitState.collectAsState()

            HabitsGraph(
                state = state,
                onAction = DummyStateProvider::onHabitAction,
                isUserSubscribed = true,
                onNavigateToPaywall = {}
            )
        }
    }
}