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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.core.tasks.TaskWithSubTasks
import com.shub39.grit.shared.ui.components.GritBottomSheet
import com.shub39.grit.shared.ui.task.ui.section.UpdateStatusTarget
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.add_subtasks
import grit.shared.ui.generated.resources.unlock_plus
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubTasksFeaturePreview(
    modifier: Modifier = Modifier,
    is24Hr: Boolean = false,
    onDismissRequest: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    val sampleTask = remember {
        Task(id = 100, categoryId = 1, title = "Weekly Grocery Shopping", status = false)
    }

    val reminderTime1 = remember {
        LocalDateTime(year = 2026, month = Month.MARCH, day = 15, hour = 10, minute = 0)
    }

    val reminderTime2 = remember {
        LocalDateTime(year = 2026, month = Month.MARCH, day = 15, hour = 14, minute = 30)
    }

    val subTask1 = remember {
        SubTask(id = 1, taskId = 100, title = "Buy fresh produce", status = false, reminder = null)
    }

    val subTask2 = remember {
        SubTask(
            id = 2,
            taskId = 100,
            title = "Pick up dairy & eggs",
            status = false,
            reminder = reminderTime1,
        )
    }

    val subTask3 = remember {
        SubTask(
            id = 3,
            taskId = 100,
            title = "Grab bakery items",
            status = false,
            reminder = reminderTime2,
        )
    }

    var currentSubTasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentSubTasks = emptyList()
            delay(1200.milliseconds)

            currentSubTasks = listOf(subTask1)
            delay(1400.milliseconds)

            currentSubTasks = listOf(subTask1, subTask2)
            delay(1400.milliseconds)

            currentSubTasks = listOf(subTask1, subTask2, subTask3)
            delay(1600.milliseconds)

            currentSubTasks = listOf(subTask1.copy(status = true), subTask2, subTask3)
            delay(1200.milliseconds)

            currentSubTasks =
                listOf(subTask1.copy(status = true), subTask2.copy(status = true), subTask3)
            delay(2200.milliseconds)
        }
    }

    GritBottomSheet(modifier = modifier, padding = 24.dp, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.add_subtasks),
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontRounded()),
                textAlign = TextAlign.Center,
            )

            Text(
                text =
                    "Break tasks into manageable subtasks, set individual reminders, and track your progress effortlessly.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(265.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                TaskCard(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    taskWithSubTasks =
                        TaskWithSubTasks(task = sampleTask, subTasks = currentSubTasks),
                    dragState = false,
                    reorderIcon = {},
                    is24Hr = is24Hr,
                    shape = MaterialTheme.shapes.medium,
                    onUpdateStatus = { target ->
                        when (target) {
                            is UpdateStatusTarget.SubTaskStatus -> {
                                currentSubTasks =
                                    currentSubTasks.map {
                                        if (it.id == target.subTask.id) {
                                            it.copy(status = !it.status)
                                        } else it
                                    }
                            }

                            UpdateStatusTarget.TaskStatus -> {}
                        }
                    },
                    onEdit = {},
                    onShowSubTasksSheet = {},
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onOpenPaywall,
                modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
            ) {
                Text(
                    text = stringResource(Res.string.unlock_plus),
                    style =
                        ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight)
                            .copy(fontFamily = flexFontRounded()),
                )
            }
        }
    }
}
