package com.shub39.grit.tasks.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.GritDialog
import com.shub39.grit.tasks.domain.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (Task) -> Unit,
) {
    var taskStatus by remember { mutableStateOf(task.status) }
    var showEditDialog by remember { mutableStateOf(false) }

    val cardContent by animateColorAsState(
        targetValue = when (taskStatus) {
            true -> Color.Gray
            else -> MaterialTheme.colorScheme.secondary
        },
        label = "cardContent"
    )
    val cardContainer by animateColorAsState(
        targetValue = when (taskStatus) {
            true -> Color.LightGray
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "cardContainer"
    )
    val cardColors = CardDefaults.cardColors(
        containerColor = cardContainer,
        contentColor = cardContent
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .combinedClickable(
                // change status on click
                onClick = {
                    taskStatus = !taskStatus
                    task.status = !task.status
                    onStatusChange(task)
                },
                // edit on click and hold
                onLongClick = {
                    showEditDialog = true
                }
            ),
        colors = cardColors,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = task.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }

    // edit dialog
    if (showEditDialog) {
        var newTitle by remember { mutableStateOf(task.title) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritDialog(
            onDismissRequest = { showEditDialog = false }
        ) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Button(
                onClick = {
                    task.title = newTitle
                    onStatusChange(task)
                    showEditDialog = false
                }
            ) {
                Text(stringResource(R.string.edit_task))
            }
        }
    }
}