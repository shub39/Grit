package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.shared_ui.GritBottomSheet
import com.shub39.grit.core.shared_ui.GritDialog
import com.shub39.grit.core.utils.toFormattedString
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add_habit
import grit.shared.core.generated.resources.add_reminder
import grit.shared.core.generated.resources.alarm
import grit.shared.core.generated.resources.description
import grit.shared.core.generated.resources.done
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_habit
import grit.shared.core.generated.resources.save
import grit.shared.core.generated.resources.select_time
import grit.shared.core.generated.resources.title
import grit.shared.core.generated.resources.too_long
import grit.shared.core.generated.resources.update_description
import grit.shared.core.generated.resources.update_title
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
expect fun HabitUpsertSheet(
    habit: Habit,
    onDismissRequest: () -> Unit,
    onUpsertHabit: (Habit) -> Unit,
    is24Hr: Boolean,
    modifier: Modifier = Modifier,
    save: Boolean = false
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitUpsertSheetContent(
    newHabit: Habit,
    updateHabit: (Habit) -> Unit,
    onDismissRequest: () -> Unit,
    onUpsertHabit: (Habit) -> Unit,
    is24Hr: Boolean,
    save: Boolean = false,
    notificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var timePickerDialog by remember { mutableStateOf(false) }

    GritBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Icon(
                    imageVector = vectorResource(Res.drawable.edit),
                    contentDescription = "Edit Habit"
                )
            }

            item {
                Text(
                    text = stringResource(if (save) Res.string.edit_habit else Res.string.add_habit),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }

            item {
                OutlinedTextField(
                    value = newHabit.title,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = { updateHabit(newHabit.copy(title = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        if (newHabit.title.length <= 20) {
                            Text(text = stringResource(if (save) Res.string.update_title else Res.string.title))
                        } else {
                            Text(text = stringResource(Res.string.too_long))
                        }
                    },
                    isError = newHabit.title.length > 20
                )
            }

            item {
                OutlinedTextField(
                    value = newHabit.description,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { updateHabit(newHabit.copy(description = it)) },
                    label = {
                        if (newHabit.description.length <= 50) {
                            Text(text = stringResource(if (save) Res.string.update_description else Res.string.description))
                        } else {
                            Text(text = stringResource(Res.string.too_long))
                        }
                    },
                    isError = newHabit.description.length > 50
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.add_reminder),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = newHabit.reminder,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (notificationPermission) {
                                    updateHabit(newHabit.copy(reminder = true))
                                } else {
                                    onRequestPermission()
                                }
                            } else {
                                updateHabit(newHabit.copy(reminder = false))
                            }
                        }
                    )
                }
            }

            item {
                if (newHabit.reminder) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.alarm),
                                contentDescription = "Alarm Icon"
                            )

                            Text(
                                text = newHabit.time.time.toFormattedString(is24Hr = is24Hr),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        FilledTonalIconButton(
                            onClick = { timePickerDialog = true }
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.edit),
                                contentDescription = "Pick Time"
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        DayOfWeek.entries.forEach { dayOfWeek ->
                            ToggleButton(
                                checked = newHabit.days.contains(dayOfWeek),
                                onCheckedChange = {
                                    updateHabit(
                                        newHabit.copy(
                                            days = if (it) {
                                                newHabit.days + dayOfWeek
                                            } else {
                                                newHabit.days - dayOfWeek
                                            }
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                                shapes = when (dayOfWeek) {
                                    DayOfWeek.MONDAY -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    DayOfWeek.SUNDAY -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                                content = {
                                    Text(
                                        text = dayOfWeek.name.take(1),
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        onUpsertHabit(newHabit)
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = newHabit.description.length <= 50 &&
                            newHabit.title.length <= 20 &&
                            newHabit.title.isNotBlank(),
                ) {
                    Text(text = stringResource(if (save) Res.string.save else Res.string.add_habit))
                }
            }
        }

        if (timePickerDialog) {
            val timePickerState = rememberTimePickerState(
                initialHour = newHabit.time.hour,
                initialMinute = newHabit.time.minute,
                is24Hour = is24Hr
            )

            GritDialog(
                onDismissRequest = { timePickerDialog = false }
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.alarm),
                    contentDescription = "Add Time"
                )

                Text(
                    text = stringResource(Res.string.select_time),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(vertical = 4.dp))

                TimeInput(
                    state = timePickerState
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            updateHabit(
                                newHabit.copy(
                                    time = LocalDateTime(
                                        date = newHabit.time.date,
                                        time = LocalTime(
                                            minute = timePickerState.minute,
                                            hour = timePickerState.hour
                                        )
                                    )
                                )
                            )
                            timePickerDialog = false
                        },
                        shapes = ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small
                        )
                    ) {
                        Text(stringResource(Res.string.done))
                    }
                }
            }
        }
    }
}