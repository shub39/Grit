package com.shub39.grit.page

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitCard
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.habit.timePickerStateToLocalDateTime
import com.shub39.grit.notification.showAddNotification
import com.shub39.grit.viewModel.HabitViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(habitViewModel: HabitViewModel, context: Context) {
    val habits by habitViewModel.habits.collectAsState()
    val habitListIsEmpty = habits.isEmpty()
    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.habits),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_add_24),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        }
    ) { paddingValues ->
        if (showAddHabitDialog) {
            var newHabitName by remember { mutableStateOf("") }
            var newHabitDescription by remember { mutableStateOf("") }
            val timePickerState = remember { TimePickerState(12, 0, false) }
            val isHabitPresent = { habits.any { it.id == newHabitName } }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

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
                            isError = newHabitName.length > 20 || isHabitPresent(),
                            modifier = Modifier.focusRequester(focusRequester)
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.add_habit))
                    }
                }
            )
        }


        if (habitListIsEmpty) {
            EmptyPage(paddingValues = paddingValues)
        } else {
            HabitsList(
                viewModel = habitViewModel,
                paddingValue = paddingValues,
            )
        }
    }
}

@Composable
private fun HabitsList(
    viewModel: HabitViewModel,
    paddingValue: PaddingValues,
) {
    val habits by viewModel.habits.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(paddingValue)
            .animateContentSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                habitViewModel = viewModel
            )
        }
    }
}