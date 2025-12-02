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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
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
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.cancel
import grit.shared.core.generated.resources.delete
import grit.shared.core.generated.resources.delete_warning
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun AnalyticsPage(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    val windowSizeClass = LocalWindowSizeClass.current

    val primary = MaterialTheme.colorScheme.primary
    val currentMonth = remember { YearMonth.now() }

    val currentHabit = state.habitsWithAnalytics.find { it.habit.id == state.analyticsHabitId } ?: return@PageFill

    val heatMapState = rememberHeatMapCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )
    val weekDayBreakdownData = remember(currentHabit.weekDayFrequencyData) {
        prepareWeekDayDataToBars(currentHabit.weekDayFrequencyData, primary)
    }

    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        TopAppBar(
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            title = {
                Text(text = currentHabit.habit.title)
            },
            subtitle = {
                if (currentHabit.habit.description.isNotEmpty()) {
                    Text(text = currentHabit.habit.description)
                }
            },
            windowInsets = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                WindowInsets(0)
            } else {
                TopAppBarDefaults.windowInsets
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            },
            actions = {
                OutlinedIconButton(
                    onClick = { deleteDialog = true },
                    shapes = IconButtonShapes(
                        shape = CircleShape,
                        pressedShape = MaterialTheme.shapes.small
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete Habit"
                    )
                }

                FilledIconButton(
                    onClick = { editDialog = true },
                    shapes = IconButtonShapes(
                        shape = CircleShape,
                        pressedShape = MaterialTheme.shapes.small
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Habit"
                    )
                }
            }
        )

        val maxWidth = 400.dp
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 400.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            item {
                HabitStartCard(
                    habit = currentHabit.habit,
                    startedDaysAgo = currentHabit.startedDaysAgo,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }

            item {
                HabitStreakCard(
                    currentStreak = currentHabit.currentStreak,
                    bestStreak = currentHabit.bestStreak,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }

            item {
                WeeklyBooleanHeatMap(
                    heatMapState = heatMapState,
                    onAction = onAction,
                    currentHabit = currentHabit,
                    primary = primary,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }

            item {
                WeeklyActivity(
                    primary = primary,
                    lineChartData = currentHabit.weeklyComparisonData,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }

            item {
                CalendarMap(
                    canSeeContent = state.isUserSubscribed,
                    onAction = onAction,
                    calendarState = calendarState,
                    currentHabit = currentHabit,
                    primary = primary,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }

            item {
                WeekDayBreakdown(
                    canSeeContent = state.isUserSubscribed,
                    onAction = onAction,
                    weekDayData = weekDayBreakdownData,
                    primary = primary,
                    modifier = Modifier.widthIn(max = maxWidth)
                )
            }
        }
    }

    // delete dialog
    if (deleteDialog) {
        GritDialog(
            onDismissRequest = { deleteDialog = false }
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = "Warning"
            )

            Text(
                text = stringResource(Res.string.delete),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = stringResource(Res.string.delete_warning),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        deleteDialog = false
                    },
                    shapes = ButtonShapes(
                        shape = MaterialTheme.shapes.extraLarge,
                        pressedShape = MaterialTheme.shapes.small
                    )
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
                    shapes = ButtonShapes(
                        shape = MaterialTheme.shapes.extraLarge,
                        pressedShape = MaterialTheme.shapes.small
                    )
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
            save = true
        )
    }
}