package com.shub39.grit.habits.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.Empty
import com.shub39.grit.core.presentation.components.GritBottomSheet
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.showAddNotification
import com.shub39.grit.core.presentation.timePickerStateToLocalDateTime
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitsList(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val context = LocalContext.current

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showAllAnalytics by remember { mutableStateOf(false) }

    var editState by remember { mutableStateOf(false) }

    var habits by remember(state.habitsWithStatuses) {
        mutableStateOf(state.habitsWithStatuses.entries.toList())
    }
    var reorder by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            habits = habits.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }

    DisposableEffect(Unit) {
        onDispose {
            if (reorder) {
                onAction(HabitsPageAction.ReorderHabits(habits.mapIndexed { index, entry -> index to entry.key }))
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.habits))
                },
                subtitle = {
                    Column {
                        Text(
                            text = "${state.completedHabits.size}/${state.habitsWithStatuses.size} " + stringResource(
                                R.string.completed
                            )
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.habitsWithStatuses.isNotEmpty()
                    ) {
                        FilledTonalIconToggleButton(
                            checked = editState,
                            shapes = IconToggleButtonShapes(
                                shape = CircleShape,
                                checkedShape = MaterialTheme.shapes.small,
                                pressedShape = MaterialTheme.shapes.extraSmall,
                            ),
                            onCheckedChange = { editState = it },
                            enabled = state.habitsWithStatuses.isNotEmpty()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_reorder_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                // habits
                itemsIndexed(habits, key = { _, it -> it.key.id }) { index, habit ->
                    ReorderableItem(reorderableListState, key = habit.key.id) {
                        val cardCorners by animateDpAsState(
                            targetValue = if (it) 30.dp else 20.dp
                        )

                        HabitCard(
                            habit = habit.key,
                            statusList = habit.value,
                            completed = state.completedHabits.contains(habit.key),
                            action = onAction,
                            startingDay = state.startingDay,
                            editState = editState,
                            onNavigateToAnalytics = onNavigateToAnalytics,
                            timeFormat = state.timeFormat,
                            reorderHandle = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                    contentDescription = "Drag Indicator",
                                    modifier = Modifier.draggableHandle(
                                        onDragStopped = { reorder = true }
                                    )
                                )
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(cardCorners)
                        )
                    }
                }

                // when no habits
                if (state.habitsWithStatuses.isEmpty() && !showAddHabitDialog) {
                    item { Empty() }
                }

                // ui sweetener
                item { Spacer(modifier = Modifier.padding(60.dp)) }
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = state.habitsWithStatuses.isNotEmpty()
            ) {
                FloatingActionButton(
                    onClick = { showAllAnalytics = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_analytics_24),
                        contentDescription = "All Analytics"
                    )
                }
            }

            FloatingActionButton(
                onClick = { showAddHabitDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_add_24),
                        contentDescription = "Add Habit"
                    )

                    AnimatedVisibility(
                        visible = state.habitsWithStatuses.isEmpty()
                    ) {
                        Text(text = stringResource(id = R.string.add_habit))
                    }
                }
            }
        }
    }

    // all analytics
    if (showAllAnalytics) {
        AllAnalyticsSheet(
            state = state,
            onDismiss = { showAllAnalytics = false }
        )
    }

    // add dialog
    if (showAddHabitDialog) {
        var newHabitName by remember { mutableStateOf("") }
        var newHabitDescription by remember { mutableStateOf("") }
        var newHabitTime by remember { mutableStateOf(LocalDateTime.now()) }
        val isHabitPresent = { state.habitsWithStatuses.any { it.key.title == newHabitName } }

        var timePickerDialog by remember { mutableStateOf(false) }

        GritBottomSheet(
            onDismissRequest = { showAddHabitDialog = false }
        ) {
            Icon(
                painter = painterResource(R.drawable.round_add_24),
                contentDescription = "Add Habit"
            )

            Text(
                text = stringResource(R.string.add_habit),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newHabitName,
                onValueChange = { newHabitName = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    if (isHabitPresent()) {
                        Text(text = stringResource(id = R.string.habit_exists))
                    } else if (newHabitName.length <= 20) {
                        Text(text = stringResource(id = R.string.title))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitName.length > 20 || isHabitPresent()
            )

            OutlinedTextField(
                value = newHabitDescription,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitDescription = it },
                label = {
                    if (newHabitName.length <= 50) {
                        Text(text = stringResource(id = R.string.description))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitName.length > 50
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
                        text = newHabitTime.format(DateTimeFormatter.ofPattern(state.timeFormat)),
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
                    val habitEntity = Habit(
                        title = newHabitName,
                        description = newHabitDescription,
                        time = newHabitTime,
                        index = habits.size,
                        days = emptySet()
                    )
                    showAddHabitDialog = false
                    onAction(HabitsPageAction.AddHabit(habitEntity))
                    showAddNotification(context, habitEntity)
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newHabitName.isNotBlank() && newHabitDescription.isNotBlank() && newHabitName.length < 20 && newHabitDescription.length < 50,
            ) {
                Text(text = stringResource(id = R.string.add_habit))
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
                            newHabitTime = timePickerStateToLocalDateTime(timePickerState)
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