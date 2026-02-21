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
package com.shub39.grit.core.shared_ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.clock
import grit.shared.core.generated.resources.done
import grit.shared.core.generated.resources.keyboard
import grit.shared.core.generated.resources.select_time
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GritTimePicker(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    onConfirm: () -> Unit,
) {
    var timeInput by remember { mutableStateOf(false) }

    TimePickerDialog(
        modifier = modifier.animateContentSize(animationSpec = tween(0)),
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.select_time)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(Res.string.done)) }
        },
        modeToggleButton = {
            IconButton(onClick = { timeInput = !timeInput }) {
                Icon(
                    imageVector =
                        vectorResource(
                            if (!timeInput) Res.drawable.keyboard else Res.drawable.clock
                        ),
                    contentDescription = null,
                )
            }
        },
    ) {
        if (timeInput) {
            TimeInput(state = state)
        } else {
            TimePicker(state = state)
        }
    }
}
