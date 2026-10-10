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
package com.shub39.grit.shared.ui.task.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.grit.core.tasks.TaskWithSubTasks
import com.shub39.grit.core.toFormattedString
import com.shub39.grit.shared.ui.components.segmentedListItemShapes
import com.shub39.grit.shared.ui.task.ui.section.ShowSubTaskUpsertSheet
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    taskWithSubTasks: TaskWithSubTasks,
    dragState: Boolean = false,
    reorderIcon: @Composable () -> Unit,
    is24Hr: Boolean,
    shape: Shape = RoundedCornerShape(4.dp),
    onUpdateStatus: () -> Unit,
    onEdit: () -> Unit,
    onShowSubTasksSheet: (ShowSubTaskUpsertSheet) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var touchOffset by remember { mutableStateOf(DpOffset.Zero) }

    val density = LocalDensity.current

    val cardContent by
        animateColorAsState(
            targetValue =
                when (taskWithSubTasks.task.status) {
                    true -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardContent",
        )
    val cardContainer by
        animateColorAsState(
            targetValue =
                when (taskWithSubTasks.task.status) {
                    true -> MaterialTheme.colorScheme.surfaceContainerHighest
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardContainer",
        )
    val cardColors =
        CardDefaults.cardColors(containerColor = cardContainer, contentColor = cardContent)

    Box(modifier = modifier) {
        Column {
            Card(
                modifier =
                    Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    touchOffset = with(density) { DpOffset(it.x.toDp(), 0.dp) }
                                    expanded = true
                                },
                                onTap = { onUpdateStatus() },
                            )
                        }
                        .clip(shape),
                colors = cardColors,
                shape = shape,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = taskWithSubTasks.task.title,
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            textDecoration =
                                if (taskWithSubTasks.task.status) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                },
                        )

                        if (taskWithSubTasks.task.reminder != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.alarm),
                                    contentDescription = "Reminder",
                                    modifier = Modifier.size(12.dp),
                                )

                                Text(
                                    text =
                                        taskWithSubTasks.task.reminder!!.toFormattedString(is24Hr),
                                    style =
                                        typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Light,
                                        ),
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = dragState, enter = fadeIn(), exit = fadeOut()) {
                        reorderIcon()
                    }
                }
            }

            if (taskWithSubTasks.subTasks.isNotEmpty()) {
                AnimatedVisibility(
                    visible = !dragState,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        taskWithSubTasks.subTasks.forEachIndexed { index, subTask ->
                            val subTaskShape =
                                segmentedListItemShapes(index, taskWithSubTasks.subTasks.size).shape

                            Card(
                                modifier =
                                    Modifier.pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    // TODO: Edit Subtask status
                                                },
                                                onLongPress = {
                                                    onShowSubTasksSheet(
                                                        ShowSubTaskUpsertSheet.Edit(subTask)
                                                    )
                                                },
                                            )
                                        }
                                        .clip(subTaskShape),
                                shape = subTaskShape,
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    Text(
                                        text = subTask.title,
                                        style =
                                            typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        textDecoration =
                                            if (subTask.status) {
                                                TextDecoration.LineThrough
                                            } else {
                                                TextDecoration.None
                                            },
                                    )

                                    if (subTask.reminder != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.alarm),
                                                contentDescription = "Reminder",
                                                modifier = Modifier.size(12.dp),
                                            )

                                            Text(
                                                text = subTask.reminder!!.toFormattedString(is24Hr),
                                                style =
                                                    typography.labelSmall.copy(
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Light,
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            containerColor = Color.Transparent,
            offset = touchOffset,
            border = null,
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(Res.string.edit_task)) },
                onClick = {
                    onEdit()
                    expanded = false
                },
                colors = MenuDefaults.selectableItemColors(),
                leadingIcon = {
                    Icon(imageVector = vectorResource(Res.drawable.edit), contentDescription = null)
                },
                shape = MenuDefaults.leadingItemShape,
            )

            Spacer(modifier = Modifier.height(2.dp))

            DropdownMenuItem(
                text = { Text(text = stringResource(Res.string.add_subtasks)) },
                onClick = {
                    onShowSubTasksSheet(ShowSubTaskUpsertSheet.Add(taskWithSubTasks.task))
                    expanded = false
                },
                colors = MenuDefaults.selectableItemColors(),
                leadingIcon = {
                    Icon(imageVector = vectorResource(Res.drawable.add), contentDescription = null)
                },
                shape = MenuDefaults.trailingItemShape,
            )
        }
    }
}
