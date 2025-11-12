package com.shub39.grit.habits.presentation

import androidx.compose.animation.core.tween
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
import com.materialkolor.PaletteStyle
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Fonts
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.core.presentation.theme.Theme
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitWithAnalytics
import com.shub39.grit.habits.presentation.ui.section.AnalyticsPage
import com.shub39.grit.habits.presentation.ui.section.HabitsList
import com.shub39.grit.habits.presentation.ui.section.OverallAnalytics
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit
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

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    val habitsWithStatuses = (0L..1L).map { habitId ->
        HabitWithAnalytics(
            habit = Habit(
                id = habitId,
                title = "Habit $habitId",
                description = "Description $habitId",
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                days = emptySet(),
                index = habitId.toInt(),
                reminder = false
            ),
            statuses = emptyList(),
            weeklyComparisonData = emptyList(),
            weekDayFrequencyData = emptyMap(),
            currentStreak = 100,
            bestStreak = 100,
            startedDaysAgo = 12,
        )
    }

    var state by remember { mutableStateOf(HabitPageState(
        habitsWithAnalytics = habitsWithStatuses,
        isUserSubscribed = true
    )) }

    GritTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            isMaterialYou = true,
            font = Fonts.FIGTREE,
            paletteStyle = PaletteStyle.Fidelity
        )
    ) {
        Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                HabitsGraph(
                    state = state,
                    onAction = {}
                )
            }
        }
    }
}