package com.shub39.grit.tasks.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.GritBottomSheet
import com.shub39.grit.tasks.domain.Task

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (Task) -> Unit,
    dragState: Boolean = false,
    reorderIcon: @Composable () -> Unit,
    shape: Shape,
    modifier: Modifier
) {
    var taskStatus by remember { mutableStateOf(task.status) }
    var showEditSheet by remember { mutableStateOf(false) }

    val cardContent by animateColorAsState(
        targetValue = when (taskStatus) {
            true -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.secondary
        },
        label = "cardContent"
    )
    val cardContainer by animateColorAsState(
        targetValue = when (taskStatus) {
            true -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "cardContainer"
    )
    val cardColors = CardDefaults.cardColors(
        containerColor = cardContainer,
        contentColor = cardContent
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .combinedClickable(
                // change status on click
                onClick = {
                    if (!dragState) {
                        taskStatus = !taskStatus
                        task.status = !task.status
                        onStatusChange(task)
                    }
                },
                // edit on click and hold
                onLongClick = {
                    if (!dragState) {
                        showEditSheet = true
                    }
                }
            ),
        colors = cardColors,
        shape = shape,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textDecoration = if (taskStatus) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            AnimatedVisibility(
                visible = dragState
            ) {
                reorderIcon()
            }
        }
    }

    // edit dialog
    if (showEditSheet) {
        var newTitle by remember { mutableStateOf(task.title) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = { showEditSheet = false }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )

            Text(
                text = stringResource(id = R.string.edit_task),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        newTitle.plus("\n")
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text(text = stringResource(id = R.string.edit_task)) }
            )

            Button(
                onClick = {
                    task.title = newTitle
                    onStatusChange(task)
                    showEditSheet = false
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newTitle.isNotBlank() && newTitle.length <= 100
            ) {
                Text(stringResource(R.string.edit_task))
            }
        }
    }
}