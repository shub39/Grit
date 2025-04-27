package com.shub39.grit.habits.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsPage(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    val primary = MaterialTheme.colorScheme.primary
    val today = LocalDate.now()
    val currentMonth = remember { YearMonth.now() }

    val currentHabit by remember {
        mutableStateOf(state.habitsWithStatuses.keys.find { it.id == state.analyticsHabitId }!!)
    }
    val statuses by remember {
        mutableStateOf(state.habitsWithStatuses[currentHabit]!!)
    }

    val heatMapState = rememberHeatMapCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )

    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        MediumTopAppBar(
            title = {
                Column {
                    Text(
                        text = currentHabit.title
                    )
                    Text(
                        text = currentHabit.description,
                        style = MaterialTheme.typography.bodySmall
                    )
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
                IconButton(
                    onClick = { deleteDialog = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_delete_forever_24),
                        contentDescription = "Delete Habit"
                    )
                }

                IconButton(
                    onClick = { editDialog = true }
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp)
        ) {
            item {
                AnalyticsCard {
                    HeatMapCalendar(
                        state = heatMapState,
                        weekHeader = {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(30.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
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
                            var done by rememberSaveable {
                                mutableStateOf(statuses.any { it.date == day.date })
                            }

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
                                            shape = MaterialTheme.shapes.large
                                        ) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (done) primary else if (day.date > today) Color.Transparent else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                }
            }

            item {
                AnalyticsCard {

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
                contentDescription = "Warning",
            )

            Text(
                text = stringResource(R.string.delete),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.delete_warning),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    deleteDialog = false
                    onNavigateBack()
                    onAction(HabitsPageAction.DeleteHabit(currentHabit))
                }
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }

    if (editDialog) {
        var newHabitTitle by remember { mutableStateOf(currentHabit.title) }
        var newHabitDescription by remember { mutableStateOf(currentHabit.description) }
        val timePickerState = remember {
            TimePickerState(
                initialHour = currentHabit.time.hour,
                initialMinute = currentHabit.time.minute,
                is24Hour = state.is24Hr
            )
        }

        GritDialog(
            onDismissRequest = { editDialog = false }
        ) {
            OutlinedTextField(
                value = newHabitTitle,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitTitle = it },
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

            TimePicker(
                state = timePickerState
            )

            Button(
                onClick = {
                    editDialog = false
                    onAction(
                        HabitsPageAction.UpdateHabit(
                            Habit(
                                id = currentHabit.id,
                                title = newHabitTitle,
                                description = newHabitDescription,
                                time = currentHabit.time.withHour(timePickerState.hour)
                                    .withMinute(timePickerState.minute),
                                index = currentHabit.index
                            )
                        )
                    )
                },
                enabled = newHabitDescription.isNotBlank() && newHabitDescription.length <= 50 && newHabitTitle.length <= 20 && newHabitTitle.isNotBlank(),
            ) {
                Text(text = stringResource(id = R.string.update))
            }
        }
    }
}