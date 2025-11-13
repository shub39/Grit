package com.shub39.grit.core.habits.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.sections.AnalyticsPage
import com.shub39.grit.core.habits.presentation.ui.sections.HabitsList
import com.shub39.grit.core.habits.presentation.ui.sections.OverallAnalytics
import kotlinx.serialization.Serializable

@Serializable
private sealed interface HabitRoutes {
    @Serializable
    data object HabitList : HabitRoutes

    @Serializable
    data object HabitAnalytics : HabitRoutes

    @Serializable
    data object OverallAnalytics : HabitRoutes
}

@Composable
fun HabitsGraph(
    state: HabitState,
    onAction: (HabitsAction) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HabitRoutes.HabitList,
        enterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        composable<HabitRoutes.HabitList> {
            HabitsList(
                state = state,
                onAction = onAction,
                onNavigateToOverallAnalytics = { navController.navigate(HabitRoutes.OverallAnalytics) },
                onNavigateToAnalytics = { navController.navigate(HabitRoutes.HabitAnalytics) }
            )
        }

        composable<HabitRoutes.HabitAnalytics> {
            AnalyticsPage(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<HabitRoutes.OverallAnalytics> {
            OverallAnalytics(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}