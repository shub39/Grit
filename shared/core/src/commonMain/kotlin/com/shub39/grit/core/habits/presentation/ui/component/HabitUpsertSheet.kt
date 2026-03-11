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
package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.shared_ui.GritBottomSheet
import com.shub39.grit.core.shared_ui.GritTimePicker
import com.shub39.grit.core.shared_ui.detachedItemShape
import com.shub39.grit.core.shared_ui.endItemShape
import com.shub39.grit.core.shared_ui.leadingItemShape
import com.shub39.grit.core.shared_ui.listItemColors
import com.shub39.grit.core.shared_ui.middleItemShape
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.theme.flexFontRounded
import com.shub39.grit.core.utils.now
import com.shub39.grit.core.utils.toFormattedString
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.add_habit
import grit.shared.core.generated.resources.add_reminder
import grit.shared.core.generated.resources.add_reminder_desc
import grit.shared.core.generated.resources.alarm
import grit.shared.core.generated.resources.description
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_habit
import grit.shared.core.generated.resources.save
import grit.shared.core.generated.resources.select_days
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
    isEditSheet: Boolean = false,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitUpsertSheetContent(
    newHabit: Habit,
    updateHabit: (Habit) -> Unit,
    onDismissRequest: () -> Unit,
    onUpsertHabit: (Habit) -> Unit,
    is24Hr: Boolean,
    isEditSheet: Boolean = false,
    notificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var timePickerDialog by remember { mutableStateOf(false) }

    GritBottomSheet(
        onDismissRequest = onDismissRequest,
        padding = 0.dp,
        modifier = modifier.imePadding(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(50.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                        ),
            ) {
                Icon(
                    imageVector =
                        vectorResource(if (isEditSheet) Res.drawable.edit else Res.drawable.add),
                    contentDescription = "Edit Habit",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Text(
                text =
                    stringResource(
                        if (isEditSheet) Res.string.edit_habit else Res.string.add_habit
                    ),
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontEmphasis()),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                OutlinedTextField(
                    value = newHabit.title,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions =
                        KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done,
                        ),
                    onValueChange = { updateHabit(newHabit.copy(title = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        if (newHabit.title.length <= 20) {
                            Text(
                                text =
                                    stringResource(
                                        if (isEditSheet) Res.string.update_title
                                        else Res.string.title
                                    )
                            )
                        } else {
                            Text(text = stringResource(Res.string.too_long))
                        }
                    },
                    isError = newHabit.title.length > 20,
                )
            }

            item {
                OutlinedTextField(
                    value = newHabit.description,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions =
                        KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { updateHabit(newHabit.copy(description = it)) },
                    label = {
                        if (newHabit.description.length <= 50) {
                            Text(
                                text =
                                    stringResource(
                                        if (isEditSheet) Res.string.update_description
                                        else Res.string.description
                                    )
                            )
                        } else {
                            Text(text = stringResource(Res.string.too_long))
                        }
                    },
                    isError = newHabit.description.length > 50,
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Card(
                        shape =
                            if (newHabit.days.isEmpty()) detachedItemShape()
                            else leadingItemShape(),
                        modifier = Modifier.animateContentSize(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(text = stringResource(Res.string.select_days))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                            ) {
                                DayOfWeek.entries.forEach { dayOfWeek ->
                                    ToggleButton(
                                        checked = newHabit.days.contains(dayOfWeek),
                                        onCheckedChange = {
                                            updateHabit(
                                                newHabit.copy(
                                                    days =
                                                        if (it) {
                                                            newHabit.days + dayOfWeek
                                                        } else {
                                                            newHabit.days - dayOfWeek
                                                        }
                                                )
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                                        content = { Text(text = dayOfWeek.name.take(1)) },
                                    )
                                }
                            }
                        }
                    }

                    if (newHabit.days.isNotEmpty()) {
                        ListItem(
                            colors = listItemColors(),
                            modifier =
                                Modifier.clip(
                                    if (newHabit.reminder) middleItemShape() else endItemShape()
                                ),
                            headlineContent = {
                                Text(text = stringResource(Res.string.add_reminder))
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.add_reminder_desc),
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(),
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.alarm),
                                    contentDescription = "Alarm Icon",
                                )
                            },
                            trailingContent = {
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
                                    },
                                )
                            },
                        )

                        if (newHabit.reminder) {
                            ListItem(
                                colors = listItemColors(),
                                modifier = Modifier.clip(endItemShape()),
                                headlineContent = {
                                    Text(
                                        text =
                                            newHabit.time.time.toFormattedString(is24Hr = is24Hr),
                                        style =
                                            MaterialTheme.typography.titleLarge.copy(
                                                fontFamily = flexFontRounded()
                                            ),
                                    )
                                },
                                trailingContent = {
                                    FilledTonalIconButton(onClick = { timePickerDialog = true }) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.edit),
                                            contentDescription = "Pick Time",
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                onUpsertHabit(newHabit)
                onDismissRequest()
            },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp).fillMaxWidth(),
            enabled =
                newHabit.description.length <= 50 &&
                    newHabit.title.length <= 20 &&
                    newHabit.title.isNotBlank(),
        ) {
            Text(text = stringResource(if (isEditSheet) Res.string.save else Res.string.add_habit))
        }

        if (timePickerDialog) {
            val timePickerState =
                rememberTimePickerState(
                    initialHour = newHabit.time.hour,
                    initialMinute = newHabit.time.minute,
                    is24Hour = is24Hr,
                )

            GritTimePicker(
                onDismissRequest = { timePickerDialog = false },
                state = timePickerState,
                onConfirm = {
                    updateHabit(
                        newHabit.copy(
                            time =
                                LocalDateTime(
                                    date = newHabit.time.date,
                                    time =
                                        LocalTime(
                                            minute = timePickerState.minute,
                                            hour = timePickerState.hour,
                                        ),
                                )
                        )
                    )
                    timePickerDialog = false
                },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        HabitUpsertSheet(
            habit =
                Habit(
                    id = 1,
                    title = "New Habit",
                    description = "A new Habit",
                    time = LocalDateTime.now(),
                    days = DayOfWeek.entries.toSet(),
                    index = 1,
                    reminder = false,
                ),
            onDismissRequest = {},
            onUpsertHabit = {},
            is24Hr = true,
            isEditSheet = true,
        )
    }
}
