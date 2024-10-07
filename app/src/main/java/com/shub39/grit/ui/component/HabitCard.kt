package com.shub39.grit.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.logic.OtherLogic.localToTimePickerState
import com.shub39.grit.viewModel.HabitViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habit: Habit,
    habitViewModel: HabitViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showAnalyticsSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var completedStatus by rememberSaveable { mutableStateOf(false) }

    val cardContent by animateColorAsState(
        targetValue = when (completedStatus) {
            true -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondary
        }
    )
    val cardBackground by animateColorAsState(
        targetValue = when (completedStatus) {
            true -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    )
    val cardColors = CardDefaults.cardColors(
        contentColor = cardContent,
        containerColor = cardBackground
    )

    val updateLambda = {
        if (!completedStatus) {
            habitViewModel.insertHabitStatus(habit.id)
            completedStatus = true
        } else {
            habitViewModel.deleteHabitStatus(habit.id)
            completedStatus = false
        }
    }

    LaunchedEffect(Unit, showAnalyticsSheet) {
        if (habitViewModel.isHabitCompleted(habitId = habit.id)) {
            completedStatus = true
        }
    }

    Card(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            .combinedClickable(
                onClick = { updateLambda() },
                onLongClick = { showEditDialog = true },
                onDoubleClick = { showAnalyticsSheet = true }
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = cardColors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = completedStatus,
                onCheckedChange = { updateLambda() }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.id,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                painter = painterResource(R.drawable.round_alarm_24),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = habit.time.format(DateTimeFormatter.ofPattern("hh:mm a"))
            )
        }

    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Column {
                    Icon(
                        painter = painterResource(id = R.drawable.round_warning_24),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(64.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.delete_warning),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(id = R.string.all_data_lost),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Button(onClick = {
                        showDeleteDialog = false
                        habitViewModel.deleteHabit(habit)
                    }) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                }
            }
        )
    }

    if (showAnalyticsSheet) {
        AnalyticsSheet(
            habit = habit,
            vm = habitViewModel,
            onDismiss = { showAnalyticsSheet = false }
        )
    }

    if (showEditDialog) {
        var newHabitDescription by remember { mutableStateOf(habit.description) }
        val timePickerState = remember { localToTimePickerState(habit.time) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            text = {
                Column {
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

                    Spacer(modifier = Modifier.padding(8.dp))

                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showEditDialog = false
                            showDeleteDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 4.dp,
                            bottomEnd = 4.dp
                        )
                    ) {
                        Text(text = stringResource(id = R.string.delete))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = {
                            showEditDialog = false
                            habitViewModel.updateHabit(
                                Habit(
                                    habit.id,
                                    newHabitDescription,
                                    habit.time.withHour(timePickerState.hour)
                                        .withMinute(timePickerState.minute)
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            bottomStart = 4.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp
                        ),
                        enabled = newHabitDescription.isNotBlank() && newHabitDescription.length <= 50,
                    ) {
                        Text(text = stringResource(id = R.string.update))
                    }
                }
            }
        )
    }
}