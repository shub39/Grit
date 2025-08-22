package com.shub39.grit.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.R
import com.shub39.grit.billing.PaywallPage
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsGraph
import com.shub39.grit.habits.presentation.HabitsGraph
import com.shub39.grit.tasks.presentation.TasksGraph
import com.shub39.grit.viewmodels.HabitViewModel
import com.shub39.grit.viewmodels.SettingsViewModel
import com.shub39.grit.viewmodels.TasksViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
private sealed interface Routes {
    @Serializable
    data object HabitsPages : Routes

    @Serializable
    data object TaskPages : Routes

    @Serializable
    data object SettingsPages : Routes

    companion object {
        val mainRoutes = listOf(
            TaskPages,
            HabitsPages,
            SettingsPages
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Grit(
    svm: SettingsViewModel
) {
    val navController = rememberNavController()
    var currentRoute: Routes by remember { mutableStateOf(Routes.TaskPages) }

    val settingsState by svm.state.collectAsStateWithLifecycle()

    val navigator = { route: Routes ->
        if (currentRoute != route) {
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo(
                    when (settingsState.startingPage) {
                        Pages.Habits -> Routes.HabitsPages
                        Pages.Tasks -> Routes.TaskPages
                    }
                ) { saveState = true }
                restoreState = true
            }
        }
    }

    AnimatedContent(
        targetState = settingsState.showPaywall,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        if (!it) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        )
                    ) {
                        Routes.mainRoutes.forEach { route ->
                            NavigationBarItem(
                                selected = currentRoute == route,
                                onClick = { navigator(route) },
                                icon = {
                                    Icon(
                                        painter = painterResource(
                                            when (route) {
                                                Routes.HabitsPages -> R.drawable.round_alarm_24
                                                Routes.TaskPages -> R.drawable.round_checklist_24
                                                else -> R.drawable.round_settings_24
                                            }
                                        ),
                                        contentDescription = null,
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(
                                            when (route) {
                                                Routes.HabitsPages -> R.string.habits
                                                Routes.TaskPages -> R.string.tasks
                                                else -> R.string.settings
                                            }
                                        )
                                    )
                                },
                                alwaysShowLabel = false
                            )
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = when (settingsState.startingPage) {
                        Pages.Tasks -> Routes.TaskPages
                        Pages.Habits -> Routes.HabitsPages
                    },
                    modifier = Modifier
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background),
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                    composable<Routes.TaskPages> {
                        currentRoute = Routes.TaskPages
                        val tvm: TasksViewModel = koinViewModel()
                        val taskPageState by tvm.state.collectAsStateWithLifecycle()

                        TasksGraph(
                            state = taskPageState,
                            onAction = tvm::taskPageAction
                        )
                    }

                    composable<Routes.SettingsPages> {
                        currentRoute = Routes.SettingsPages

                        SettingsGraph(
                            state = settingsState,
                            onAction = svm::onAction,
                        )
                    }

                    composable<Routes.HabitsPages> {
                        currentRoute = Routes.HabitsPages

                        val hvm: HabitViewModel = koinViewModel()
                        val habitsPageState by hvm.state.collectAsStateWithLifecycle()

                        HabitsGraph(
                            state = habitsPageState,
                            onAction = hvm::habitsPageAction,
                        )
                    }
                }

            }
        } else {
            PaywallPage(
                isPlusUser = settingsState.isUserSubscribed,
                onDismissRequest = { svm.onAction(SettingsAction.OnPaywallDismiss) }
            )
        }
    }
}