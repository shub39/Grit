package com.shub39.grit.core.shared_ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        modifier = modifier.animateContentSize(),
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.select_time))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.done))
            }
        },
        modeToggleButton = {
            IconButton(
                onClick = { timeInput = !timeInput }
            ) {
                Icon(
                    imageVector = vectorResource(
                        if (!timeInput) Res.drawable.keyboard else Res.drawable.clock
                    ),
                    contentDescription = null
                )
            }
        }
    ) {
        AnimatedContent(
            targetState = timeInput
        ) {
            if (it) {
                TimeInput(state = state)
            } else {
                TimePicker(
                    state = state,
                    layoutType = TimePickerLayoutType.Vertical,
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .heightIn(max = 800.dp)
                )
            }
        }
    }
}