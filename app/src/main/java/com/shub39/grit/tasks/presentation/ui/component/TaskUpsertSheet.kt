package com.shub39.grit.tasks.presentation.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskUpsertSheet(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    is24Hr: Boolean,
    modifier: Modifier = Modifier,
    edit: Boolean = false
) {
    val context = LocalContext.current

    var newTask by remember { mutableStateOf(task) }
    var showDateTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        is24Hour = is24Hr
    )
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.now()
    )
    val isValidDateTime = newTask.reminder?.isAfter(LocalDateTime.now()) ?: true

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showDateTimePicker = true
        else Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
    }

    GritBottomSheet(
        modifier = modifier,
        padding = 0.dp,
        onDismissRequest = onDismissRequest
    ) {
        Icon(
            imageVector = if (edit) Icons.Rounded.Edit else Icons.Rounded.Add,
            contentDescription = "Upsert",
        )

        Text(
            text = stringResource(id = if (edit) R.string.edit_task else R.string.add_task),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

//        FlowRow(
//            horizontalArrangement = Arrangement.Center
//        ) {
//            categories.forEach { category ->

//            }
//        }

        LazyRow(
            horizontalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories) { category ->
                ToggleButton(
                    checked = category.id == newTask.categoryId,
                    onCheckedChange = { newTask = newTask.copy(categoryId = category.id) },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    content = { Text(category.name) }
                )
            }
        }

        OutlinedTextField(
            value = newTask.title,
            onValueChange = { newTask = newTask.copy(title = it) },
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.None
            ),
            keyboardActions = KeyboardActions(
                onAny = {
                    newTask = newTask.copy(title = newTask.title.plus("\n"))
                }
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.add_reminder),
                    style = MaterialTheme.typography.titleLarge,
                )

                if (newTask.reminder != null) {
                    Text(
                        text = newTask.reminder!!.format(
                            DateTimeFormatter.ofLocalizedDateTime(
                                FormatStyle.SHORT
                            )
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!isValidDateTime) {
                    Text(
                        text = stringResource(R.string.invalid_date_time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Switch(
                checked = newTask.reminder != null,
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
                            showDateTimePicker = true
                        }
                    } else {
                        newTask = newTask.copy(reminder = null)
                    }
                }
            )
        }

        Button(
            onClick = {
                onUpsert(newTask)
                onDismissRequest()
            },
            shapes = ButtonShapes(
                shape = MaterialTheme.shapes.extraLarge,
                pressedShape = MaterialTheme.shapes.small
            ),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                .fillMaxWidth(),
            enabled = newTask.title.isNotBlank()
                    && newTask.title.length <= 100
                    && newTask != task
                    && isValidDateTime
        ) {
            Text(stringResource(if (edit) R.string.edit_task else R.string.add_task))
        }
    }

    if (showDateTimePicker) {
        var showTimePicker by remember { mutableStateOf(false) }

        DatePickerDialog(
            onDismissRequest = { showDateTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        newTask = newTask.copy(
                            reminder = LocalDateTime.of(
                                datePickerState.getSelectedDate(),
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                            )
                        )
                        showDateTimePicker = false
                    }
                ) {
                    Text(stringResource(R.string.done))
                }
            },
            dismissButton = {
                IconButton(
                    onClick = { showTimePicker = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = "Select Time"
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = {},
                    title = {},
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showTimePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.done))
                        }
                    }
                ) {
                    TimePicker(
                        state = timePickerState
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        TaskUpsertSheet(
            task = Task(
                id = 1,
                categoryId = 1,
                title = "Dummy Task",
                index = 1,
                status = true,
                reminder = null
            ),
            categories = (0..10).map {
                Category(
                    id = it.toLong(),
                    name = "Dummy category $it",
                    index = it,
                    color = "Yellow :)"
                )
            },
            onDismissRequest = {  },
            onUpsert = {  },
            is24Hr = false
        )
    }
}