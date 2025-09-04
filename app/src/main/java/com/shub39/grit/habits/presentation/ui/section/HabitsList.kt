package com.shub39.grit.habits.presentation.ui.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.Empty
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.core.presentation.showAddNotification
import com.shub39.grit.core.presentation.timePickerStateToLocalDateTime
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.ui.component.HabitCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitsList(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToOverallAnalytics: () -> Unit
) = PageFill {
    val context = LocalContext.current

    var editState by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    var reorderableHabits by remember(state.habitsWithStatuses) {
        mutableStateOf(
            state.habitsWithStatuses.entries.toList()
        )
    }
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            reorderableHabits = reorderableHabits.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LargeFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
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
                            imageVector = Icons.Rounded.Reorder,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // habits
            itemsIndexed(reorderableHabits, key = { _, it -> it.key.id }) { index, habit ->
                ReorderableItem(reorderableListState, key = habit.key.id) {
                    val cardCorners by animateDpAsState(
                        targetValue = if (!it) 20.dp else 16.dp
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
                                    onDragStopped = {
                                        onAction(
                                            HabitsPageAction.ReorderHabits(reorderableHabits.mapIndexed { index, entry -> index to entry.key })
                                        )
                                    }
                                )
                            )
                        },
                        shape = RoundedCornerShape(cardCorners)
                    )
                }
            }

            // when no habits
            if (state.habitsWithStatuses.isEmpty()) {
                item { Empty() }
            }

            // ui sweetener
            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = onNavigateToOverallAnalytics,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.animateFloatingActionButton(
                visible = state.habitsWithStatuses.isNotEmpty() && fabVisible,
                alignment = Alignment.BottomEnd
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Analytics,
                contentDescription = "All Analytics"
            )
        }

        MediumFloatingActionButton(
            onClick = {
                onAction(HabitsPageAction.OnAddHabitClicked)
            },
            modifier = Modifier.animateFloatingActionButton(
                visible = fabVisible,
                alignment = Alignment.BottomEnd
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Habit",
                    modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
                )

                AnimatedVisibility(
                    visible = state.habitsWithStatuses.isEmpty()
                ) {
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        }
    }

    // add dialog
    if (state.showHabitAddSheet) {
        var newHabitName by remember { mutableStateOf("") }
        var newHabitDescription by remember { mutableStateOf("") }
        var newHabitTime by remember { mutableStateOf(LocalDateTime.now()) }
        var newHabitDays by remember { mutableStateOf(DayOfWeek.entries.toSet()) }

        var timePickerDialog by remember { mutableStateOf(false) }

        GritBottomSheet(
            onDismissRequest = {
                onAction(HabitsPageAction.DismissAddHabitDialog)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Habit"
            )

            Text(
                text = stringResource(R.string.add_habit),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newHabitName,
                singleLine = true,
                onValueChange = { newHabitName = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    if (newHabitName.length <= 20) {
                        Text(text = stringResource(id = R.string.title))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitName.length > 20
            )

            OutlinedTextField(
                value = newHabitDescription,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitDescription = it },
                label = {
                    if (newHabitDescription.length <= 50) {
                        Text(text = stringResource(id = R.string.description))
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
                    val habitEntity = Habit(
                        title = newHabitName,
                        description = newHabitDescription,
                        time = newHabitTime,
                        index = reorderableHabits.size,
                        days = newHabitDays,
                        reminder = false
                    )
                    onAction(HabitsPageAction.AddHabit(habitEntity))
                    onAction(HabitsPageAction.DismissAddHabitDialog)
                    showAddNotification(context, habitEntity)
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newHabitName.isNotBlank() && newHabitName.length <= 20 && newHabitDescription.length <= 50,
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