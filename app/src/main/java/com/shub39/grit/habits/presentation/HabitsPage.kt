package com.shub39.grit.habits.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.Empty
import com.shub39.grit.core.presentation.showAddNotification
import com.shub39.grit.core.presentation.timePickerStateToLocalDateTime
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.component.HabitGuide
import com.shub39.grit.habits.presentation.component.HabitCard
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(
    state: HabitPageState,
    action: (HabitsPageAction) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current

    // controllers, states and scopes
    var showAddHabitDialog by remember { mutableStateOf(false) }
    val addState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val addSize by animateDpAsState(
        targetValue = if (addState.distanceFraction != 0f) 64.dp else 0.dp,
        label = "button size"
    )

    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.habits))
                },
                actions = {
                    IconButton(
                        onClick =  onSettingsClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_settings_24),
                            contentDescription = null
                        )
                    }
                }
            )

            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = {
                    coroutineScope.launch {
                        addState.animateToHidden()
                    }
                    showAddHabitDialog = true
                },
                state = addState,
                indicator = {}
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    // add button
                    item {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(addSize / 4)
                                .height(addSize)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_add_24),
                                contentDescription = null
                            )
                        }
                    }

                    // habits
                    items(state.habitsWithStatuses.entries.toList(), key = { it.key.id }) {
                        HabitCard(
                            habit = it.key,
                            statusList = it.value,
                            completed = state.completedHabits.contains(it.key),
                            action = action,
                        )
                    }

                    // habit guide
                    if (state.habitsWithStatuses.size <= 1) {
                        item {
                            HabitGuide()
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
        }
    }

    // add dialog
    if (showAddHabitDialog) {
        var newHabitName by remember { mutableStateOf("") }
        var newHabitDescription by remember { mutableStateOf("") }
        val timePickerState = remember { TimePickerState(12, 0, false) }
        val isHabitPresent = { state.habitsWithStatuses.any { it.key.title == newHabitName } }

        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            text = {
                Column {
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

                    Spacer(modifier = Modifier.padding(8.dp))

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

                    Spacer(modifier = Modifier.padding(8.dp))

                    TimePicker(
                        state = timePickerState,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val habitEntity = Habit(
                            title = newHabitName,
                            description = newHabitDescription,
                            time = timePickerStateToLocalDateTime(timePickerState)
                        )
                        showAddHabitDialog = false
                        action(HabitsPageAction.AddHabit(habitEntity))
                        showAddNotification(context, habitEntity)
                    },
                    enabled = newHabitName.isNotBlank() && newHabitDescription.isNotBlank() && newHabitName.length < 20 && newHabitDescription.length < 50,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=411dp,height=891dp", showSystemUi = true
)
@Composable
private fun HabitsPagePreview() {
    HabitsPage(
        state = HabitPageState(
            habitsWithStatuses = (0L..100L).associate { habitId ->
                Habit(
                    id = habitId,
                    title = "Habit no $habitId",
                    description = "Description no $habitId",
                    time = LocalDateTime.now()
                ) to (0L..10L).map { HabitStatus(
                    id = it * 2,
                    habitId = habitId,
                    date = LocalDateTime.now().toLocalDate().minusDays(it)
                ) }
            }
        ),
        action = {},
        onSettingsClick = {}
    )
}