import Routes.Companion.toDisplayText
import Routes.Companion.toImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.habits.presentation.ui.HabitsGraph
import com.shub39.grit.core.tasks.presentation.ui.TasksGraph
import domain.StateProvider
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
private sealed interface Routes {
    @Serializable
    data object Tasks : Routes
    @Serializable
    data object Habits : Routes

    companion object {
        val allRoutes = listOf(
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    val navController = rememberNavController()

    val stateProvider: StateProvider = koinInject()
    var currentRoute: Routes by remember { mutableStateOf(Routes.Tasks) }

    DynamicMaterialTheme(
        primary = Color(0xFABD2F),
        isDark = true
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                Routes.allRoutes.forEach { route ->
                    item(
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
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Tasks
            ) {
                composable<Routes.Tasks> {
                    val state by stateProvider.taskState.collectAsState()

                    TasksGraph(
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
    }
}