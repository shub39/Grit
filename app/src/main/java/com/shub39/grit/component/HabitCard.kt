package com.shub39.grit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.database.habit.Habit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habit: Habit,
    onHabitClick: (Habit) -> Unit,
    onDeleteClick: (Habit) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        var newHabitDescription by remember { mutableStateOf(habit.description) }
        val timePickerState = remember { longToTimePickerState(habit.time) }


        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            text = {
                Column {

                    OutlinedTextField(
                        value = newHabitDescription,
                        shape = MaterialTheme.shapes.medium,
                        onValueChange = { newHabitDescription = it },
                        label = { Text(text = stringResource(id = R.string.update_description)) }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    TimeInput(
                        state = timePickerState
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        showEditDialog = false
                        onDeleteClick(habit)
                    }) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                    Button(
                        onClick = {
                            showEditDialog = false
                            onHabitClick(
                                Habit(
                                    habit.id,
                                    newHabitDescription,
                                    timePickerStateToLong(
                                        timePickerState.hour,
                                        timePickerState.minute
                                    )
                                )
                            )
                        },
                        enabled = newHabitDescription.isNotBlank() && newHabitDescription.length < 50,
                    ) {
                        Text(text = stringResource(id = R.string.update))
                    }
                }
            }
        )
    }

    Card(
        onClick = { showEditDialog = true },
        Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.id,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                colors = CardColors(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = formatDateTime(habit.time)
                )
            }
        }
    }
}

private fun formatDateTime(time: Long): String {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return localDateTime.format(formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
private fun longToTimePickerState(timeInMillis: Long): TimePickerState {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneOffset.UTC)
    return TimePickerState(localDateTime.hour, localDateTime.minute, false)
}

private fun timePickerStateToLong(hour: Int, minute: Int): Long {
    val now = LocalDateTime.now()
    val time = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    return time.toInstant(ZoneOffset.UTC).toEpochMilli()
}