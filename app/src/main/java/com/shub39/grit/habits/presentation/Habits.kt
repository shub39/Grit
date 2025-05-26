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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.component.AnalyticsPage
import com.shub39.grit.habits.presentation.component.HabitsList
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun Habits(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit
) = PageFill {
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
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    val habitsWithStatueses = (0L..10L).associate { habitId ->
        Habit(
            id = habitId,
            title = "Habit $habitId",
            description = "This is Habit no: $habitId",
            time = LocalDateTime.now().minusDays(habitId),
            index = habitId.toInt()
        ) to (0L .. 20L).map {
            HabitStatus(
                id = it,
                habitId = habitId,
                date = LocalDate.now().minusDays(it)
            )
        }
    }

    var state by remember { mutableStateOf(HabitPageState(
        habitsWithStatuses = habitsWithStatueses,
        completedHabits = habitsWithStatueses.keys.toList()
    )) }

    GritTheme {
        Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                Habits(
                    state = state,
                    onAction = {
                        when (it) {
                            is HabitsPageAction.PrepareAnalytics -> {
                                state = state.copy(
                                    analyticsHabitId = it.habit.id
                                )
                            }

                            is HabitsPageAction.InsertStatus -> {
                                state = state.copy(
                                    habitsWithStatuses = state.habitsWithStatuses.mapValues { (habit, statuses) ->
                                        if (habit.id == it.habit.id) {
                                            if (statuses.any { status -> status.date == it.date }) {
                                                statuses.filter { status -> status.date != it.date }
                                            } else {
                                                statuses + HabitStatus(habitId = habit.id, date = it.date)
                                            }
                                        } else statuses
                                    },
                                    completedHabits = if (state.completedHabits.contains(it.habit)) {
                                        state.completedHabits - it.habit
                                    } else if (it.date == LocalDate.now()) {
                                        state.completedHabits + it.habit
                                    } else state.completedHabits
                                )
                            }

                            else -> {}
                        }
                    }
                )
            }
        }
    }
}