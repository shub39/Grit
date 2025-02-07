package com.shub39.grit.habits.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.Empty
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.showAddNotification
import com.shub39.grit.core.presentation.timePickerStateToLocalDateTime
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.component.HabitCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(
    state: HabitPageState,
    action: (HabitsPageAction) -> Unit,
) {
    val context = LocalContext.current

    var showAddHabitDialog by remember { mutableStateOf(false) }

    var editState by remember { mutableStateOf(false) }

    var habits by remember(state.habitsWithStatuses) { mutableStateOf(state.habitsWithStatuses.entries.toList()) }
    val lazyListState = rememberLazyListState()
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { _, _ -> }

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
                actions = {
                    IconButton(
                        onClick = { editState = !editState },
                        colors = if (editState) {
                            IconButtonDefaults.filledIconButtonColors()
                        } else {
                            IconButtonDefaults.iconButtonColors()
                        },
                        enabled = state.habitsWithStatuses.isNotEmpty()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_reorder_24),
                            contentDescription = null
                        )
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // habits
                itemsIndexed(habits, key = { _, it -> it.key.id }) { index, habit ->
                    ReorderableItem(reorderableListState, key = { habit.key.id }) {
                        HabitCard(
                            habit = habit.key,
                            statusList = habit.value,
                            completed = state.completedHabits.contains(habit.key),
                            action = action,
                            editState = editState,
                            onMoveUp = {
                                if (index > 0) {
                                    habits = habits.toMutableList().apply {
                                        add(index - 1, removeAt(index))
                                    }

                                    action(HabitsPageAction.ReorderHabits(habits.mapIndexed { index, entry -> index to entry.key }))
                                }
                            },
                            onMoveDown = {
                                if (index < habits.size - 1) {
                                    habits = habits.toMutableList().apply {
                                        add(index + 1, removeAt(index))
                                    }

                                    action(HabitsPageAction.ReorderHabits(habits.mapIndexed { index, entry -> index to entry.key }))
                                }
                            }
                        )
                    }
                }

                // when no habits
                if (state.habitsWithStatuses.isEmpty() && !showAddHabitDialog) {
                    item {
                        Empty()
                    }
                }

                // ui sweetener
                item { Spacer(modifier = Modifier.padding(60.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { showAddHabitDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_add_24),
                    contentDescription = null
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
    if (showAddHabitDialog) {
        var newHabitName by remember { mutableStateOf("") }
        var newHabitDescription by remember { mutableStateOf("") }
        val timePickerState = remember { TimePickerState(12, 0, false) }
        val isHabitPresent = { state.habitsWithStatuses.any { it.key.title == newHabitName } }

        GritDialog(
            onDismissRequest = { showAddHabitDialog = false }
        ) {
            OutlinedTextField(
                value = newHabitName,
                onValueChange = { newHabitName = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                shape = MaterialTheme.shapes.medium,
                label = {
                    if (isHabitPresent()) {
                        Text(text = stringResource(id = R.string.habit_exists))
                    } else if (newHabitName.length <= 20) {
                        Text(text = stringResource(id = R.string.add_habit))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitName.length > 20 || isHabitPresent()
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
                    if (newHabitName.length <= 50) {
                        Text(text = stringResource(id = R.string.add_description))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitName.length > 50
            )

            TimePicker(
                state = timePickerState,
            )

            Button(
                onClick = {
                    val habitEntity = Habit(
                        title = newHabitName,
                        description = newHabitDescription,
                        time = timePickerStateToLocalDateTime(timePickerState),
                        index = habits.size
                    )
                    showAddHabitDialog = false
                    action(HabitsPageAction.AddHabit(habitEntity))
                    showAddNotification(context, habitEntity)
                },
                enabled = newHabitName.isNotBlank() && newHabitDescription.isNotBlank() && newHabitName.length < 20 && newHabitDescription.length < 50,
            ) {
                Text(text = stringResource(id = R.string.add_habit))
            }
        }
    }
}