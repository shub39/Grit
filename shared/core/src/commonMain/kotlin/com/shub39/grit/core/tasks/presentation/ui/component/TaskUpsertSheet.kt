package com.shub39.grit.core.tasks.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.shared_ui.GritBottomSheet
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.utils.now
import com.shub39.grit.core.utils.toFormattedString
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.add_reminder
import grit.shared.core.generated.resources.add_task
import grit.shared.core.generated.resources.delete
import grit.shared.core.generated.resources.done
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_task
import grit.shared.core.generated.resources.invalid_date_time
import grit.shared.core.generated.resources.save
import grit.shared.core.generated.resources.schedule
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
expect fun TaskUpsertSheet(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    onDelete: () -> Unit,
    is24Hr: Boolean,
    modifier: Modifier = Modifier,
    save: Boolean = false
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun TaskUpsertSheetContent(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    onDelete: () -> Unit,
    is24Hr: Boolean,
    save: Boolean = false,
    notificationPermission: Boolean,
    showDateTimePicker: Boolean,
    updateDateTimePickerVisibility: (Boolean) -> Unit,
    onPermissionRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var newTask by remember { mutableStateOf(task) }

    val timePickerState = rememberTimePickerState(
        is24Hour = is24Hr
    )
    val datePickerState = rememberDatePickerState()
    val isValidDateTime = if (newTask.reminder != null) {
        newTask.reminder!! > LocalDateTime.now()
    } else true

    GritBottomSheet(
        modifier = modifier,
        padding = 0.dp,
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Icon(
                    imageVector = vectorResource(if (save) Res.drawable.edit else Res.drawable.add),
                    contentDescription = "Upsert",
                )
            }

            item {
                Text(
                    text = stringResource(if (save) Res.string.edit_task else Res.string.add_task),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    itemVerticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    categories.forEach { category ->
                        ToggleButton(
                            checked = category.id == newTask.categoryId,
                            onCheckedChange = { newTask = newTask.copy(categoryId = category.id) },
                            colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                            modifier = Modifier.padding(horizontal = 4.dp),
                            content = { Text(category.name) }
                        )
                    }
                }
            }

            item {
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    delay(200)
                    focusRequester.requestFocus()
                    keyboardController?.show()
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
                        .focusRequester(focusRequester)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.add_reminder),
                            style = MaterialTheme.typography.titleLarge,
                        )

                        if (newTask.reminder != null) {
                            Text(
                                text = newTask.reminder!!.toFormattedString(is24Hr = is24Hr),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (!isValidDateTime) {
                            Text(
                                text = stringResource(Res.string.invalid_date_time),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Switch(
                        checked = newTask.reminder != null,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (notificationPermission) {
                                    updateDateTimePickerVisibility(true)
                                } else {
                                    onPermissionRequest()
                                }
                            } else {
                                newTask = newTask.copy(reminder = null)
                            }
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (save) {
                        OutlinedButton(
                            onClick = onDelete,
                            shapes = ButtonShapes(
                                shape = MaterialTheme.shapes.extraLarge,
                                pressedShape = MaterialTheme.shapes.small
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(Res.string.delete))
                        }
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
                        modifier = Modifier.weight(1f),
                        enabled = newTask.title.isNotBlank()
                                && newTask.title.length <= 100
                                && newTask != task
                                && isValidDateTime
                    ) {
                        Text(stringResource(if (save) Res.string.save else Res.string.add_task))
                    }
                }
            }
        }
    }

    if (showDateTimePicker) {
        var showTimePicker by remember { mutableStateOf(false) }

        DatePickerDialog(
            onDismissRequest = { updateDateTimePickerVisibility(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (datePickerState.selectedDateMillis != null) {
                            newTask = newTask.copy(
                                reminder = LocalDateTime(
                                    date = Instant
                                        .fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .date,
                                    time = LocalTime(
                                        hour = timePickerState.hour,
                                        minute = timePickerState.minute
                                    )
                                )
                            )

                            updateDateTimePickerVisibility(false)
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(stringResource(Res.string.done))
                }
            },
            dismissButton = {
                IconButton(
                    onClick = { showTimePicker = true }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.schedule),
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
                    onDismissRequest = { showTimePicker = false },
                    title = {},
                    confirmButton = {
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) {
                            Text(stringResource(Res.string.done))
                        }
                    },
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .heightIn(max = 500.dp)
                ) {
                    TimeInput(
                        state = timePickerState
                    )
                }
            }
        }
    }
}