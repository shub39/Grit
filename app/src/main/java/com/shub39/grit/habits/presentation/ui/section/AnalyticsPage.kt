package com.shub39.grit.habits.presentation.ui.section

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.core.presentation.countBestStreak
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.prepareLineChartData
import com.shub39.grit.habits.presentation.prepareWeekDayData
import com.shub39.grit.habits.presentation.ui.component.CalendarMap
import com.shub39.grit.habits.presentation.ui.component.HabitStartCard
import com.shub39.grit.habits.presentation.ui.component.HabitStreakCard
import com.shub39.grit.habits.presentation.ui.component.HabitUpsertSheet
import com.shub39.grit.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.habits.presentation.ui.component.WeeklyActivity
import com.shub39.grit.habits.presentation.ui.component.WeeklyBooleanHeatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnalyticsPage(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    val scope = rememberCoroutineScope()
    val primary = MaterialTheme.colorScheme.primary
    val today = LocalDate.now()
    val currentMonth = remember { YearMonth.now() }

    val currentHabit = state.habitsWithStatuses.keys.find { it.id == state.analyticsHabitId }!!
    val statuses = state.habitsWithStatuses[currentHabit]!!

    var lineChartData = prepareLineChartData(state.startingDay, statuses)
    var weekDayData = prepareWeekDayData(statuses.map { it.date }, primary)
    var currentStreak = countCurrentStreak(statuses.map { it.date }, currentHabit.days)
    var bestStreak = countBestStreak(statuses.map { it.date }, currentHabit.days)

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

    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(statuses) {
        scope.launch(Dispatchers.Default) {
            lineChartData = prepareLineChartData(state.startingDay, statuses)
            currentStreak = countCurrentStreak(statuses.map { it.date }, currentHabit.days)
            bestStreak = countBestStreak(statuses.map { it.date }, currentHabit.days)
            weekDayData = prepareWeekDayData(statuses.map { it.date }, primary)
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        MediumFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                Text(text = currentHabit.title)
            },
            subtitle = {
                if (currentHabit.description.isNotEmpty()) {
                    Text(text = currentHabit.description)
                }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                HabitStartCard(
                    today = today,
                    habitDate = currentHabit.time
                )
            }

            item {
                HabitStreakCard(
                    currentStreak = currentStreak,
                    bestStreak = bestStreak
                )
            }

            item {
                WeeklyBooleanHeatMap(
                    heatMapState = heatMapState,
                    statuses = statuses,
                    today = today,
                    onAction = onAction,
                    currentHabit = currentHabit,
                    primary = primary
                )
            }

            item {
                WeeklyActivity(
                    primary = primary,
                    lineChartData = lineChartData
                )
            }

            item {
                CalendarMap(
                    canSeeContent = state.isUserSubscribed,
                    onAction = onAction,
                    calendarState = calendarState,
                    statuses = statuses,
                    today = today,
                    currentHabit = currentHabit,
                    primary = primary
                )
            }

            item {
                WeekDayBreakdown(
                    canSeeContent = state.isUserSubscribed,
                    onAction = onAction,
                    weekDayData = weekDayData,
                    primary = primary,
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
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
                text = stringResource(R.string.delete),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.delete_warning),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    deleteDialog = false
                    onNavigateBack()
                    onAction(HabitsPageAction.DeleteHabit(currentHabit))
                },
                modifier = Modifier.fillMaxWidth(),
                shapes = ButtonShapes(
                    shape = CircleShape,
                    pressedShape = MaterialTheme.shapes.medium
                )
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }

    if (editDialog) {
        HabitUpsertSheet(
            habit = currentHabit,
            onDismissRequest = { editDialog = false },
            timeFormat = state.timeFormat,
            onUpsertHabit = { onAction(HabitsPageAction.UpdateHabit(it)) },
            is24Hr = state.is24Hr,
            edit = true
        )
    }
}