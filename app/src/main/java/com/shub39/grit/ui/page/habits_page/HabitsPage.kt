package com.shub39.grit.ui.page.habits_page

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.ui.component.Empty
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.notification.NotificationMethods.showAddNotification
import com.shub39.grit.logic.OtherLogic.timePickerStateToLocalDateTime
import com.shub39.grit.ui.page.habits_page.component.HabitGuide
import com.shub39.grit.ui.page.habits_page.component.HabitCard
import com.shub39.grit.viewModel.HabitViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(
    habitViewModel: HabitViewModel,
    context: Context
) {
    val habits by habitViewModel.habits.collectAsState()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    val addState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val addSize by animateDpAsState(
        targetValue = if (addState.distanceFraction != 0f) 64.dp else 0.dp,
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
                .padding(top = 16.dp)
                .animateContentSize()
        ) {
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

            items(habits, key = { it.id }) {
                HabitCard(
                    habit = it,
                    habitViewModel = habitViewModel,
                )
            }

            item {
                AnimatedVisibility(
                    visible = habits.size == 1,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    content = { HabitGuide() }
                )
            }

            item {
                AnimatedVisibility(
                    visible = habits.isEmpty() && !showAddHabitDialog,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    content = { Empty() }
                )
            }

            item { Spacer(modifier = Modifier.padding(60.dp)) }
        }
    }

    if (showAddHabitDialog) {
        var newHabitName by remember { mutableStateOf("") }
        var newHabitDescription by remember { mutableStateOf("") }
        val timePickerState = remember { TimePickerState(12, 0, false) }
        val isHabitPresent = { habits.any { it.id == newHabitName } }

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
                        val habit = Habit(
                            newHabitName,
                            newHabitDescription,
                            timePickerStateToLocalDateTime(timePickerState)
                        )
                        showAddHabitDialog = false
                        habitViewModel.addHabit(habit)
                        showAddNotification(context, habit)
                    },
                    enabled = newHabitName.isNotBlank() && newHabitDescription.isNotBlank() && newHabitName.length < 20 && newHabitDescription.length < 50,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        )
    }
}