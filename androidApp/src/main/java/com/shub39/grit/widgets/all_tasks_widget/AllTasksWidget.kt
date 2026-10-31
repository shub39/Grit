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
package com.shub39.grit.widgets.all_tasks_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.now
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.CategoryColors
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.core.tasks.TaskWithSubTasks
import com.shub39.grit.core.tasks.TasksStatusHandler
import com.shub39.grit.core.toFormattedString
import com.shub39.grit.widgets.WidgetSize
import com.shub39.grit.widgets.common.ListSlot
import com.shub39.grit.widgets.common.NothingToShow
import com.shub39.grit.widgets.common.SectionHeader
import com.shub39.grit.widgets.common.connectedBackground
import com.shub39.grit.widgets.common.slotOf
import com.shub39.grit.widgets.common.widgetSurface
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AllTasksWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<TaskRepo>()
        val statusHandler = get<TasksStatusHandler>()
        val datastore = get<SettingsDatastore>()

        provideContent {
            val scope = rememberCoroutineScope()
            val size = LocalSize.current
            val tasks by repo.getTasksFlow().collectAsState(initial = emptyMap())
            val is24Hr by datastore.getIs24Hr().collectAsState(initial = false)

            key(size) {
                GlanceTheme {
                    Content(
                        tasks = tasks.filter { it.value.isNotEmpty() },
                        is24Hr = is24Hr,
                        onToggleTask = { task ->
                            scope.launch {
                                statusHandler.toggleTask(task)
                                this@AllTasksWidget.update(context, id)
                            }
                        },
                        onToggleSubTask = { subTask ->
                            scope.launch {
                                statusHandler.toggleSubTask(subTask)
                                this@AllTasksWidget.update(context, id)
                            }
                        },
                        onUpdateWidget = {
                            scope.launch { this@AllTasksWidget.update(context, id) }
                        },
                    )
                }
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val category =
            Category(id = 1, name = "Chores", index = 0, color = CategoryColors.GRAY.color)

        val previewItems =
            mapOf(
                category to
                    listOf(
                        TaskWithSubTasks(
                            task =
                                Task(
                                    id = 1,
                                    categoryId = category.id,
                                    title = "Clean the kitchen",
                                    index = 0,
                                    reminder = LocalDateTime.now(),
                                ),
                            subTasks =
                                listOf(
                                    SubTask(id = 1, taskId = 1, title = "Dishes", index = 0),
                                    SubTask(id = 2, taskId = 1, title = "Trash", index = 1),
                                ),
                        ),
                        TaskWithSubTasks(
                            task =
                                Task(
                                    id = 2,
                                    categoryId = category.id,
                                    title = "Water the plants",
                                    index = 1,
                                ),
                            subTasks = emptyList(),
                        ),
                        TaskWithSubTasks(
                            task =
                                Task(
                                    id = 3,
                                    categoryId = category.id,
                                    title = "Buy groceries",
                                    index = 2,
                                    status = true,
                                ),
                            subTasks = emptyList(),
                        ),
                    )
            )

        provideContent {
            Content(
                tasks = previewItems,
                is24Hr = false,
                onToggleTask = {},
                onToggleSubTask = {},
                onUpdateWidget = {},
            )
        }
    }
}

@Composable
@GlanceComposable
private fun Content(
    tasks: Map<Category, List<TaskWithSubTasks>>,
    is24Hr: Boolean,
    onToggleTask: (Task) -> Unit,
    onToggleSubTask: (SubTask) -> Unit,
    onUpdateWidget: () -> Unit,
    modifier: GlanceModifier = GlanceModifier,
) {
    val size = LocalSize.current
    val showSubTaskCount = size.width >= WidgetSize.Width4

    Column(
        modifier =
            modifier.fillMaxSize().widgetSurface().clickable(actionStartActivity<MainActivity>())
    ) {
        TitleBar(
            startIcon = ImageProvider(R.drawable.check_list),
            title = "Tasks",
            actions = {
                if (showSubTaskCount) {
                    Box(GlanceModifier.padding(horizontal = 16.dp)) {
                        Image(
                            provider = ImageProvider(R.drawable.refresh),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                            modifier = GlanceModifier.clickable { onUpdateWidget() },
                        )
                    }
                }
            },
        )

        if (tasks.isEmpty()) {
            NothingToShow()
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                tasks.entries.forEach { (category, taskWithSubTasks) ->
                    val open = taskWithSubTasks.filter { !it.task.status }
                    val completed = taskWithSubTasks.filter { it.task.status }

                    item {
                        SectionHeader(
                            text = category.name,
                            modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    items(
                        open.mapIndexed { index, item -> index to item },
                        itemId = { it.second.task.id },
                    ) { (index, task) ->
                        // 2dp between items, matching the task list in the app
                        Column(modifier = GlanceModifier.padding(bottom = 2.dp)) {
                            TaskItem(
                                taskWithSubTasks = task,
                                slot = slotOf(index, open.size),
                                is24Hr = is24Hr,
                                showSubTaskCount = showSubTaskCount,
                                onToggleTask = onToggleTask,
                                onToggleSubTask = onToggleSubTask,
                            )
                        }
                    }

                    if (completed.isNotEmpty()) {
                        items(
                            completed.mapIndexed { index, item -> index to item },
                            itemId = { it.second.task.id },
                        ) { (index, task) ->
                            Column(modifier = GlanceModifier.padding(bottom = 2.dp)) {
                                TaskItem(
                                    taskWithSubTasks = task,
                                    slot = slotOf(index, completed.size),
                                    is24Hr = is24Hr,
                                    showSubTaskCount = showSubTaskCount,
                                    onToggleTask = onToggleTask,
                                    onToggleSubTask = onToggleSubTask,
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = GlanceModifier.height(12.dp)) }
                }

                item { Spacer(modifier = GlanceModifier.height(4.dp)) }
            }
        }
    }
}

@Composable
@GlanceComposable
private fun TaskItem(
    taskWithSubTasks: TaskWithSubTasks,
    slot: ListSlot,
    is24Hr: Boolean,
    showSubTaskCount: Boolean,
    onToggleTask: (Task) -> Unit,
    onToggleSubTask: (SubTask) -> Unit,
) {
    val status = taskWithSubTasks.task.status
    val containerColor =
        if (status) GlanceTheme.colors.tertiaryContainer else GlanceTheme.colors.secondaryContainer
    val contentColor =
        if (status) GlanceTheme.colors.onTertiaryContainer
        else GlanceTheme.colors.onSecondaryContainer

    Column {
        Row(
            modifier =
                GlanceModifier.fillMaxWidth()
                    .connectedBackground(slot, containerColor)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .clickable { onToggleTask(taskWithSubTasks.task) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = taskWithSubTasks.task.title,
                    maxLines = 2,
                    style =
                        TextStyle(
                            color = contentColor,
                            textDecoration =
                                if (status) TextDecoration.LineThrough else TextDecoration.None,
                        ),
                )

                val reminder = taskWithSubTasks.task.reminder
                if (reminder != null) {
                    Text(
                        text = reminder.time.toFormattedString(is24Hr),
                        maxLines = 1,
                        style = TextStyle(color = contentColor, fontSize = 10.sp),
                    )
                }
            }

            if (showSubTaskCount && taskWithSubTasks.subTasks.isNotEmpty()) {
                Text(
                    text =
                        "${taskWithSubTasks.subTasks.count { it.status }}/" +
                            taskWithSubTasks.subTasks.size,
                    maxLines = 1,
                    style = TextStyle(color = contentColor, fontSize = 10.sp),
                    modifier = GlanceModifier.padding(start = 8.dp),
                )
            }
        }

        val subTasks = taskWithSubTasks.subTasks
        if (subTasks.isNotEmpty()) {
            Column(modifier = GlanceModifier.padding(start = 16.dp, top = 2.dp)) {
                subTasks.forEachIndexed { index, subTask ->
                    // 2dp between subtasks, matching the subtask list in the app
                    Column(
                        modifier =
                            if (index == subTasks.lastIndex) GlanceModifier
                            else GlanceModifier.padding(bottom = 2.dp)
                    ) {
                        SubTaskItem(
                            subTask = subTask,
                            slot = slotOf(index, subTasks.size),
                            isParentDone = status,
                            is24Hr = is24Hr,
                            onToggleSubTask = onToggleSubTask,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@GlanceComposable
private fun SubTaskItem(
    subTask: SubTask,
    slot: ListSlot,
    isParentDone: Boolean,
    is24Hr: Boolean,
    onToggleSubTask: (SubTask) -> Unit,
) {
    val containerColor =
        if (subTask.status) GlanceTheme.colors.tertiaryContainer
        else GlanceTheme.colors.secondaryContainer
    val contentColor =
        if (subTask.status) GlanceTheme.colors.onTertiaryContainer
        else GlanceTheme.colors.onSecondaryContainer

    Row(
        modifier =
            GlanceModifier.fillMaxWidth()
                .connectedBackground(slot, containerColor)
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .then(
                    if (isParentDone) GlanceModifier
                    else GlanceModifier.clickable { onToggleSubTask(subTask) }
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider =
                ImageProvider(
                    if (subTask.status) R.drawable.check_circle else R.drawable.circle_border
                ),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
            modifier = GlanceModifier.size(14.dp),
        )

        Spacer(modifier = GlanceModifier.width(6.dp))

        Column {
            Text(
                text = subTask.title,
                maxLines = 1,
                style =
                    TextStyle(
                        color = contentColor,
                        textDecoration =
                            if (subTask.status) TextDecoration.LineThrough else TextDecoration.None,
                    ),
            )

            val reminder = subTask.reminder
            if (reminder != null) {
                Text(
                    text = reminder.time.toFormattedString(is24Hr),
                    maxLines = 1,
                    style = TextStyle(color = contentColor, fontSize = 10.sp),
                )
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(heightDp = 200, widthDp = 240)
@Preview(heightDp = 200, widthDp = 300)
@Composable
private fun GlancePreview() {
    val category = Category(id = 1, name = "Chores", index = 0, color = CategoryColors.GRAY.color)

    Content(
        tasks =
            mapOf(
                category to
                    listOf(
                        TaskWithSubTasks(
                            task =
                                Task(id = 1, categoryId = category.id, title = "Clean the kitchen"),
                            subTasks =
                                listOf(
                                    SubTask(id = 1, taskId = 1, title = "Dishes"),
                                    SubTask(id = 2, taskId = 1, title = "Trash", status = true),
                                ),
                        ),
                        TaskWithSubTasks(
                            task =
                                Task(
                                    id = 2,
                                    categoryId = category.id,
                                    title = "Water the plants",
                                    index = 1,
                                ),
                            subTasks = emptyList(),
                        ),
                        TaskWithSubTasks(
                            task =
                                Task(
                                    id = 3,
                                    categoryId = category.id,
                                    title = "Buy groceries",
                                    index = 2,
                                    status = true,
                                ),
                            subTasks = emptyList(),
                        ),
                    )
            ),
        is24Hr = false,
        onToggleTask = {},
        onToggleSubTask = {},
        onUpdateWidget = {},
    )
}
