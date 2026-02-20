/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.habits.presentation.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.prepareWeekDayDataToBars
import com.shub39.grit.core.habits.presentation.ui.component.CalendarMap
import com.shub39.grit.core.habits.presentation.ui.component.HabitStartCard
import com.shub39.grit.core.habits.presentation.ui.component.HabitStreakCard
import com.shub39.grit.core.habits.presentation.ui.component.HabitUpsertSheet
import com.shub39.grit.core.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.core.habits.presentation.ui.component.WeeklyActivity
import com.shub39.grit.core.habits.presentation.ui.component.WeeklyBooleanHeatMap
import com.shub39.grit.core.shared_ui.GritDialog
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.cancel
import grit.shared.core.generated.resources.delete
import grit.shared.core.generated.resources.delete_warning
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.warning
import kotlin.time.ExperimentalTime
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class,
)
@Composable
fun AnalyticsPage(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    isUserSubscribed: Boolean,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current

    val primary = MaterialTheme.colorScheme.primary
    val currentMonth = remember { YearMonth.now() }
    val currentHabit =
        state.habitsWithAnalytics.find { it.habit.id == state.analyticsHabitId } ?: return

    val heatMapState =
        rememberHeatMapCalendarState(
            startMonth = currentMonth.minusMonths(12),
            endMonth = currentMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = state.startingDay,
        )
    val calendarState =
        rememberCalendarState(
            startMonth = currentMonth.minusMonths(12),
            endMonth = currentMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = state.startingDay,
        )
    val weekDayBreakdownData =
        remember(currentHabit.weekDayFrequencyData) {
            prepareWeekDayDataToBars(currentHabit.weekDayFrequencyData, primary)
        }

    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection).fillMaxSize()) {
        TopAppBar(
            scrollBehavior = scrollBehavior,
            colors =
                TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
            title = { Text(text = currentHabit.habit.title) },
            subtitle = {
                if (currentHabit.habit.description.isNotEmpty()) {
                    Text(text = currentHabit.habit.description)
                }
            },
            windowInsets =
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    WindowInsets(0)
                } else {
                    TopAppBarDefaults.windowInsets
                },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = "Navigate Back",
                    )
                }
            },
            actions = {
                OutlinedIconButton(
                    onClick = { deleteDialog = true },
                    shapes =
                        IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.delete),
                        contentDescription = "Delete Habit",
                    )
                }

                FilledIconButton(
                    onClick = { editDialog = true },
                    shapes =
                        IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.edit),
                        contentDescription = "Edit Habit",
                    )
                }
            },
        )

        val maxWidth = 400.dp
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 400.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            item {
                HabitStartCard(
                    date = currentHabit.habit.time.date,
                    startedDaysAgo = currentHabit.startedDaysAgo,
                    modifier = Modifier.widthIn(max = maxWidth),
                )
            }

            item {
                HabitStreakCard(
                    currentStreak = currentHabit.currentStreak,
                    bestStreak = currentHabit.bestStreak,
                    modifier = Modifier.widthIn(max = maxWidth),
                )
            }

            item {
                WeeklyBooleanHeatMap(
                    heatMapState = heatMapState,
                    onAction = onAction,
                    modifier = Modifier.widthIn(max = maxWidth),
                    habit = currentHabit.habit,
                    statuses = currentHabit.statuses,
                )
            }

            item {
                WeeklyActivity(
                    lineChartData = currentHabit.weeklyComparisonData,
                    modifier = Modifier.widthIn(max = maxWidth),
                )
            }

            item {
                CalendarMap(
                    canSeeContent = isUserSubscribed,
                    onAction = onAction,
                    calendarState = calendarState,
                    currentHabit = currentHabit,
                    primary = primary,
                    modifier = Modifier.widthIn(max = maxWidth),
                    onNavigateToPaywall = onNavigateToPaywall,
                )
            }

            item {
                WeekDayBreakdown(
                    canSeeContent = isUserSubscribed,
                    weekDayData = weekDayBreakdownData,
                    onNavigateToPaywall = onNavigateToPaywall,
                    modifier = Modifier.widthIn(max = maxWidth),
                )
            }
        }
    }

    // delete dialog
    if (deleteDialog) {
        GritDialog(onDismissRequest = { deleteDialog = false }) {
            Icon(imageVector = vectorResource(Res.drawable.warning), contentDescription = "Warning")

            Text(
                text = stringResource(Res.string.delete),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(text = stringResource(Res.string.delete_warning), textAlign = TextAlign.Center)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { deleteDialog = false },
                    shapes =
                        ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Text(stringResource(Res.string.cancel))
                }

                TextButton(
                    onClick = {
                        onAction(HabitsAction.DeleteHabit(currentHabit.habit))
                        onAction(HabitsAction.PrepareAnalytics(null))
                        deleteDialog = false
                        onNavigateBack()
                    },
                    shapes =
                        ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Text(stringResource(Res.string.delete))
                }
            }
        }
    }

    if (editDialog) {
        HabitUpsertSheet(
            habit = currentHabit.habit,
            onDismissRequest = { editDialog = false },
            onUpsertHabit = { onAction(HabitsAction.UpdateHabit(it)) },
            is24Hr = state.is24Hr,
            isEditSheet = true,
        )
    }
}
