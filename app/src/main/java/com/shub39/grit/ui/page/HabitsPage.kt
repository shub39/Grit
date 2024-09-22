package com.shub39.grit.ui.page

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.ui.component.EmptyPage
import com.shub39.grit.ui.component.HabitsList
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.logic.NotificationMethods.showAddNotification
import com.shub39.grit.logic.OtherLogic.timePickerStateToLocalDateTime
import com.shub39.grit.viewModel.HabitViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(
    habitViewModel: HabitViewModel = koinViewModel(),
    context: Context
) {
    val habits by habitViewModel.habits.collectAsState()
    val habitListIsEmpty = habits.isEmpty()
    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                shape = MaterialTheme.shapes.extraLarge
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
                }
            }
        }
    ) { paddingValues ->
        if (habitListIsEmpty) {

            EmptyPage(paddingValues = paddingValues)

        } else {

            HabitsList(paddingValue = paddingValues)

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
}