package com.shub39.grit.habits.presentation.ui.section

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.core.presentation.countBestStreak
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.prepareLineChartData
import com.shub39.grit.habits.presentation.prepareWeekDayData
import com.shub39.grit.habits.presentation.ui.component.CalendarMap
import com.shub39.grit.habits.presentation.ui.component.HabitStartCard
import com.shub39.grit.habits.presentation.ui.component.HabitStreakCard
import com.shub39.grit.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.habits.presentation.ui.component.WeeklyActivity
import com.shub39.grit.habits.presentation.ui.component.WeeklyBooleanHeatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
        var newHabitTitle by remember { mutableStateOf(currentHabit.title) }
        var newHabitDescription by remember { mutableStateOf(currentHabit.description) }
        var newHabitTime by remember { mutableStateOf(currentHabit.time) }
        var newHabitDays by remember { mutableStateOf(currentHabit.days) }

        var timePickerDialog by remember { mutableStateOf(false) }

        GritBottomSheet(
            onDismissRequest = { editDialog = false }
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit Habit"
            )

            Text(
                text = stringResource(R.string.edit_habit),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newHabitTitle,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitTitle = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    if (newHabitTitle.length <= 20) {
                        Text(text = stringResource(id = R.string.update_title))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitTitle.length > 20
            )

            OutlinedTextField(
                value = newHabitDescription,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { newHabitDescription = it },
                label = {
                    if (newHabitDescription.length <= 50) {
                        Text(text = stringResource(id = R.string.update_description))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitDescription.length > 50
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Alarm,
                        contentDescription = "Alarm Icon"
                    )

                    Text(
                        text = newHabitTime.format(DateTimeFormatter.ofPattern(state.timeFormat)),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FilledTonalIconButton(
                    onClick = { timePickerDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Create,
                        contentDescription = "Pick Time"
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                DayOfWeek.entries.forEach { dayOfWeek ->
                    ToggleButton(
                        checked = newHabitDays.contains(dayOfWeek),
                        onCheckedChange = {
                            newHabitDays = if (it) {
                                newHabitDays + dayOfWeek
                            } else {
                                newHabitDays - dayOfWeek
                            }
                        },
                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        content = {
                            Text(
                                text = dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                )
                            )
                        }
                    )
                }
            }

            Button(
                onClick = {
                    editDialog = false
                    onAction(
                        HabitsPageAction.UpdateHabit(
                            Habit(
                                id = currentHabit.id,
                                title = newHabitTitle,
                                description = newHabitDescription,
                                time = newHabitTime,
                                index = currentHabit.index,
                                days = newHabitDays,
                                reminder = false
                            )
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = newHabitDescription.length <= 50 && newHabitTitle.length <= 20 && newHabitTitle.isNotBlank(),
            ) {
                Text(text = stringResource(id = R.string.update))
            }

            if (timePickerDialog) {
                val timePickerState = rememberTimePickerState(
                    initialHour = newHabitTime.hour,
                    initialMinute = newHabitTime.minute,
                    is24Hour = state.is24Hr
                )

                GritDialog(
                    onDismissRequest = { timePickerDialog = false }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Alarm,
                        contentDescription = "Add Time"
                    )

                    Text(
                        text = stringResource(R.string.select_time),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    TimeInput(
                        state = timePickerState
                    )

                    Button(
                        onClick = {
                            newHabitTime = newHabitTime.withHour(timePickerState.hour)
                                .withMinute(timePickerState.minute)
                            timePickerDialog = false
                        },
                        shapes = ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.done))
                    }
                }
            }
        }
    }
}