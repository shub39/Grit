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
package com.shub39.grit.shared.ui.habit.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.shared.ui.components.GritDialog
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.add
import grit.shared.ui.generated.resources.add_status
import grit.shared.ui.generated.resources.done
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HabitStatusDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    habit: Habit,
    value: Double,
    onAddStatus: (Double) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val textFieldState =
        rememberTextFieldState(
            initialText = value.toString(),
            initialSelection = TextRange(value.toString().length),
        )

    val isValidInput = textFieldState.text.toString().toDoubleOrNull()?.let { it >= 0.0 } ?: false

    LaunchedEffect(Unit) {
        delay(400.milliseconds)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    GritDialog(onDismissRequest = onDismissRequest, modifier = modifier) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                        ),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.add),
                    contentDescription = "Add Status",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.add_status),
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontEmphasis()),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions =
                    KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                label = { Text(text = habit.targetUnit) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                isError = textFieldState.text.isNotBlank() && !isValidInput,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onAddStatus(textFieldState.text.toString().toDoubleOrNull() ?: 1.0)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValidInput,
            ) {
                Text(text = stringResource(Res.string.done))
            }
        }
    }
}
