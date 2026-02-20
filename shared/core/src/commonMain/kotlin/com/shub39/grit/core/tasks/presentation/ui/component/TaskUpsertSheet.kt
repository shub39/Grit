/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.tasks.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.shub39.grit.core.shared_ui.GritTimePicker
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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
expect fun TaskUpsertSheet(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    onDelete: () -> Unit,
    is24Hr: Boolean,
    modifier: Modifier = Modifier,
    isEditSheet: Boolean = false,
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class,
)
@Composable
fun TaskUpsertSheetContent(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    onDelete: () -> Unit,
    is24Hr: Boolean,
    isEditSheet: Boolean = false,
    notificationPermission: Boolean,
    showDateTimePicker: Boolean,
    updateDateTimePickerVisibility: (Boolean) -> Unit,
    onPermissionRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var newTask by remember { mutableStateOf(task) }

    val timePickerState = rememberTimePickerState(is24Hour = is24Hr)
    val datePickerState = rememberDatePickerState()
    val isValidDateTime =
        if (newTask.reminder != null) {
            newTask.reminder!! > LocalDateTime.now()
        } else true

    GritBottomSheet(
        modifier = modifier.imePadding(),
        padding = 0.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector =
                    vectorResource(if (isEditSheet) Res.drawable.edit else Res.drawable.add),
                contentDescription = "Upsert",
            )

            Text(
                text =
                    stringResource(if (isEditSheet) Res.string.edit_task else Res.string.add_task),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            HorizontalDivider()
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    categories.forEach { category ->
                        ToggleButton(
                            checked = category.id == newTask.categoryId,
                            onCheckedChange = { newTask = newTask.copy(categoryId = category.id) },
                            colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                            modifier = Modifier.padding(horizontal = 4.dp),
                            content = { Text(category.name) },
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
                    keyboardOptions =
                        KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.None,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onAny = { newTask = newTask.copy(title = newTask.title.plus("\n")) }
                        ),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.add_reminder),
                            style = MaterialTheme.typography.titleLarge,
                        )

                        if (newTask.reminder != null) {
                            Text(
                                text = newTask.reminder!!.toFormattedString(is24Hr = is24Hr),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        if (!isValidDateTime) {
                            Text(
                                text = stringResource(Res.string.invalid_date_time),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
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
                        },
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider()

            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isEditSheet) {
                    OutlinedButton(
                        onClick = onDelete,
                        shapes =
                            ButtonShapes(
                                shape = MaterialTheme.shapes.extraLarge,
                                pressedShape = MaterialTheme.shapes.small,
                            ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(Res.string.delete))
                    }
                }

                Button(
                    onClick = {
                        onUpsert(newTask)
                        onDismissRequest()
                    },
                    shapes =
                        ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                    modifier = Modifier.weight(1f),
                    enabled =
                        newTask.title.isNotBlank() &&
                            newTask.title.length <= 100 &&
                            newTask != task &&
                            isValidDateTime,
                ) {
                    Text(stringResource(if (isEditSheet) Res.string.save else Res.string.add_task))
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
                            newTask =
                                newTask.copy(
                                    reminder =
                                        LocalDateTime(
                                            date =
                                                Instant.fromEpochMilliseconds(
                                                        datePickerState.selectedDateMillis!!
                                                    )
                                                    .toLocalDateTime(TimeZone.UTC)
                                                    .date,
                                            time =
                                                LocalTime(
                                                    hour = timePickerState.hour,
                                                    minute = timePickerState.minute,
                                                ),
                                        )
                                )

                            updateDateTimePickerVisibility(false)
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null,
                ) {
                    Text(stringResource(Res.string.done))
                }
            },
            dismissButton = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.schedule),
                        contentDescription = "Select Time",
                    )
                }
            },
        ) {
            DatePicker(state = datePickerState)

            if (showTimePicker) {
                GritTimePicker(
                    onDismissRequest = { showTimePicker = false },
                    state = timePickerState,
                    onConfirm = { showTimePicker = false },
                )
            }
        }
    }
}
