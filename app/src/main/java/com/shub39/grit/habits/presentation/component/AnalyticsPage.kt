package com.shub39.grit.habits.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.GritBottomSheet
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.countBestStreak
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnalyticsPage(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    val primary = MaterialTheme.colorScheme.primary
    val today = LocalDate.now()
    val currentMonth = remember { YearMonth.now() }

    val currentHabit = state.habitsWithStatuses.keys.find { it.id == state.analyticsHabitId }!!
    val statuses = state.habitsWithStatuses[currentHabit]!!

    var lineChartData = prepareLineChartData(state.startingDay, statuses)
    var currentStreak = countCurrentStreak(statuses.map { it.date })
    var bestStreak = countBestStreak(statuses.map { it.date })

    val heatMapState = rememberHeatMapCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )

    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(statuses) {
        lineChartData = prepareLineChartData(state.startingDay, statuses)
        currentStreak = countCurrentStreak(statuses.map { it.date })
        bestStreak = countBestStreak(statuses.map { it.date })
    }

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = currentHabit.title)
            },
            subtitle = {
                Text(text = currentHabit.description)
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
                        painter = painterResource(id = R.drawable.round_delete_forever_24),
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
                        painter = painterResource(id = R.drawable.baseline_edit_square_24),
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
                ListItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        overlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        supportingColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_flag_circle_24),
                            contentDescription = "Flag",
                            modifier = Modifier.size(64.dp)
                        )
                    },
                    overlineContent = {
                        Text(text = stringResource(R.string.started_on))
                    },
                    headlineContent = {
                        Text(
                            text = formatDateWithOrdinal(currentHabit.time.toLocalDate()),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                R.string.days_ago_format,
                                ChronoUnit.DAYS.between(currentHabit.time.toLocalDate(), today)
                            )
                        )
                    }
                )
            }

            item {
                ListItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        overlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        supportingColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.round_local_fire_department_24),
                            contentDescription = "Streak",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(64.dp)
                        )
                    },
                    overlineContent = {
                        Text(text = stringResource(R.string.streak))
                    },
                    headlineContent = {
                        Text(
                            text = currentStreak.toString(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.best_streak, bestStreak)
                        )
                    }
                )
            }

            item {
                AnalyticsCard {
                    HeatMapCalendar(
                        state = heatMapState,
                        monthHeader = {
                            Box(
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = it.yearMonth.month.getDisplayName(
                                        TextStyle.SHORT_STANDALONE,
                                        Locale.getDefault()
                                    ),
                                    color = MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        },
                        weekHeader = {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(30.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = MaterialTheme.shapes.large
                                    )
                            ) {
                                Text(
                                    text = it.name.take(1),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        },
                        dayContent = { day, week ->
                            var done = statuses.any { it.date == day.date }

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(30.dp)
                                    .clickable(enabled = day.date <= today) {
                                        done = !done
                                        onAction(
                                            HabitsPageAction.InsertStatus(
                                                currentHabit,
                                                day.date
                                            )
                                        )
                                    }
                                    .then(
                                        if (done) Modifier.background(
                                            color = primary.copy(alpha = 0.2f),
                                            shape = MaterialTheme.shapes.small
                                        ) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                                    color = if (done) primary else if (day.date > today) MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    ) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                }
            }

            item {
                AnalyticsCard(
                    modifier = Modifier.height(300.dp)
                ) {
                    if (lineChartData.isNotEmpty()) {
                        LineChart(
                            labelHelperProperties = LabelHelperProperties(
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
                            ),
                            gridProperties = GridProperties(
                                enabled = true,
                                xAxisProperties = GridProperties.AxisProperties(lineCount = 10),
                                yAxisProperties = GridProperties.AxisProperties(lineCount = 7)
                            ),
                            data = listOf(
                                Line(
                                    label = stringResource(R.string.weekly_graph),
                                    values = lineChartData,
                                    color = SolidColor(primary),
                                    dotProperties = DotProperties(
                                        enabled = true,
                                        color = SolidColor(primary),
                                        strokeWidth = 4.dp,
                                        radius = 7.dp,
                                        strokeColor = SolidColor(primary.copy(alpha = 0.5f))
                                    ),
                                    drawStyle = DrawStyle.Stroke(
                                        width = 3.dp,
                                        strokeStyle = StrokeStyle.Dashed(
                                            intervals = floatArrayOf(
                                                10f,
                                                10f
                                            ), phase = 15f
                                        )
                                    )
                                )
                            ),
                            curvedEdges = false,
                            animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.not_enough_data))
                        }
                    }
                }
            }
        }
    }

    // delete dialog
    if (deleteDialog) {
        GritDialog(
            onDismissRequest = { deleteDialog = false }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_warning_24),
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

        var timePickerDialog by remember { mutableStateOf(false) }

        GritBottomSheet(
            onDismissRequest = { editDialog = false }
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Edit Habit"
            )

            Text(
                text = stringResource(R.string.edit_habit),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newHabitTitle,
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
                isError = newHabitDescription.length > 20
            )

            OutlinedTextField(
                value = newHabitDescription,
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
                        painter = painterResource(R.drawable.round_alarm_24),
                        contentDescription = "Alarm Icon"
                    )

                    Text(
                        text = newHabitTime.format(
                            DateTimeFormatter.ofPattern(
                                if (state.is24Hr) "HH:mm" else "hh:mm a"
                            )
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FilledTonalIconButton(
                    onClick = { timePickerDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Pick Time"
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
                                index = currentHabit.index
                            )
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = newHabitDescription.isNotBlank() && newHabitDescription.length <= 50 && newHabitTitle.length <= 20 && newHabitTitle.isNotBlank(),
            ) {
                Text(text = stringResource(id = R.string.update))
            }

            if (timePickerDialog) {
                val timePickerState = rememberTimePickerState()

                GritDialog(
                    onDismissRequest = { timePickerDialog = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_alarm_24),
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