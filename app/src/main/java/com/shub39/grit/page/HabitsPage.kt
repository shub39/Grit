package com.shub39.grit.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitCard
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.viewModel.HabitViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(habitViewModel: HabitViewModel) {
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
                        fontWeight = FontWeight.Bold
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

            AlertDialog(
                onDismissRequest = { showAddHabitDialog = false },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newHabitName,
                            onValueChange = { newHabitName = it },
                            shape = MaterialTheme.shapes.medium,
                            label = { Text(text = stringResource(id = R.string.add_habit)) },
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        OutlinedTextField(
                            value = newHabitDescription,
                            shape = MaterialTheme.shapes.medium,
                            onValueChange = { newHabitDescription = it },
                            label = { Text(text = stringResource(id = R.string.add_description)) }
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TimeInput(
                            state = timePickerState
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAddHabitDialog = false
                            habitViewModel.addHabit(Habit(newHabitName, newHabitDescription, timePickerStateToLong(timePickerState.hour, timePickerState.minute)))
                        },
                        enabled = newHabitName.isNotBlank() && newHabitDescription.isNotBlank() && newHabitName.length < 10 && newHabitDescription.length < 50,
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
        modifier = Modifier.padding(paddingValue),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(habits, key = { it.id }) {habit ->
            HabitCard(
                habit = habit,
                onHabitClick = {
                    viewModel.updateHabit(it)
                },
                onDeleteClick = {
                    viewModel.deleteHabit(it)
                }
            )
        }
    }
}


private fun timePickerStateToLong(hour: Int, minute: Int): Long {
    val now = LocalDateTime.now()
    val time = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    return time.toInstant(ZoneOffset.UTC).toEpochMilli()
}