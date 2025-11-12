package com.shub39.grit.habits.presentation.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.habits.domain.Habit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    FormatStringsInDatetimeFormats::class
)
@Composable
fun HabitUpsertSheet(
    habit: Habit,
    onDismissRequest: () -> Unit,
    timeFormat: String,
    onUpsertHabit: (Habit) -> Unit,
    is24Hr: Boolean,
    modifier: Modifier = Modifier,
    edit: Boolean = false
) {
    val context = LocalContext.current

    var newHabit by remember { mutableStateOf(habit) }

    var timePickerDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
          newHabit = newHabit.copy(reminder = true)
        } else Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
    }

    GritBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Edit Habit"
        )

        Text(
            text = stringResource(if (edit) R.string.edit_habit else R.string.add_habit),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = newHabit.title,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            onValueChange = { newHabit = newHabit.copy(title = it) },
            modifier = Modifier.fillMaxWidth(),
            label = {
                if (newHabit.title.length <= 20) {
                    Text(text = stringResource(id = if (edit) R.string.update_title else R.string.title))
                } else {
                    Text(text = stringResource(id = R.string.too_long))
                }
            },
            isError = newHabit.title.length > 20
        )

        OutlinedTextField(
            value = newHabit.description,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { newHabit = newHabit.copy(description = it) },
            label = {
                if (newHabit.description.length <= 50) {
                    Text(text = stringResource(id = if (edit) R.string.update_description else R.string.description))
                } else {
                    Text(text = stringResource(id = R.string.too_long))
                }
            },
            isError = newHabit.description.length > 50
        )

        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.add_reminder),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = newHabit.reminder,
                onCheckedChange = { checked ->
                    if (checked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            newHabit = newHabit.copy(reminder = true)
                        }
                    } else {
                        newHabit = newHabit.copy(reminder = false)
                    }
                }
            )
        }

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
                        imageVector = Icons.Rounded.Alarm,
                        contentDescription = "Alarm Icon"
                    )

                    Text(
                        text = newHabit.time.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FilledTonalIconButton(
                    onClick = { timePickerDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Create,
                        contentDescription = "Pick Time"
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                DayOfWeek.entries.forEach { dayOfWeek ->
                    ToggleButton(
                        checked = newHabit.days.contains(dayOfWeek),
                        onCheckedChange = {
                            newHabit = newHabit.copy(
                                days = if (it) {
                                    newHabit.days + dayOfWeek
                                } else {
                                    newHabit.days - dayOfWeek
                                }
                            )
                        },
                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        content = {
                            Text(
                                text = dayOfWeek.name.take(3)
                            )
                        }
                    )
                }
            }
        }

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
            Text(text = stringResource(id = if (edit) R.string.update else R.string.add_habit))
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
                    imageVector = Icons.Rounded.Alarm,
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
                        newHabit = newHabit.copy(
                            time = LocalDateTime(
                                date = newHabit.time.date,
                                time = LocalTime(
                                    minute = timePickerState.minute,
                                    hour = timePickerState.hour
                                )
                            )
                        )
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