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
package com.shub39.grit.shared.ui.task.ui.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.CategoryColors
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isCompact
import com.shub39.grit.shared.ui.WindowSize.Companion.isExpanded
import com.shub39.grit.shared.ui.components.Empty
import com.shub39.grit.shared.ui.components.GritDialog
import com.shub39.grit.shared.ui.components.PageFill
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.genericSaver
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.task.TaskAction
import com.shub39.grit.shared.ui.task.TaskState
import com.shub39.grit.shared.ui.task.ui.component.CategoryUpsertSheet
import com.shub39.grit.shared.ui.task.ui.component.SubTaskUpsertSheet
import com.shub39.grit.shared.ui.task.ui.component.SubTasksFeaturePreview
import com.shub39.grit.shared.ui.task.ui.component.TaskCard
import com.shub39.grit.shared.ui.task.ui.component.TaskUpsertSheet
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import grit.shared.ui.generated.resources.add
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

sealed interface ShowTaskUpsertSheet {
    data object Add : ShowTaskUpsertSheet

    data class Edit(val task: Task) : ShowTaskUpsertSheet
}

sealed interface ShowSubTaskUpsertSheet {
    data class Add(val parentTask: Task) : ShowSubTaskUpsertSheet

    data class Edit(val subTask: SubTask) : ShowSubTaskUpsertSheet
}

sealed interface UpdateStatusTarget {
    data object TaskStatus : UpdateStatusTarget

    data class SubTaskStatus(val subTask: SubTask) : UpdateStatusTarget
}

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    isPlusUser: Boolean,
    onOpenPaywall: () -> Unit,
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onEditCategories: () -> Unit,
) =
    PageFill(modifier = modifier) {
        val windowSizeClass = LocalWindowSizeClass.current

        var showCategoryAddSheet by rememberSaveable { mutableStateOf(false) }
        var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
        var editState by rememberSaveable { mutableStateOf(false) }
        var showSubTasksFeaturePreview by rememberSaveable { mutableStateOf(false) }

        var showTaskUpsertSheet: ShowTaskUpsertSheet? by
            rememberSaveable(stateSaver = genericSaver<ShowTaskUpsertSheet?>()) {
                mutableStateOf(null)
            }
        var showSubTaskUpsertSheet: ShowSubTaskUpsertSheet? by
            rememberSaveable(stateSaver = genericSaver<ShowSubTaskUpsertSheet?>()) {
                mutableStateOf(null)
            }

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Column(
            modifier =
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
        ) {
            TaskListTopBar(
                state = state,
                scrollBehavior = scrollBehavior,
                isReorderMode = editState,
                onReorderToggle = { editState = it },
                onDeleteClick = { showDeleteDialog = true },
                isExpanded = windowSizeClass.isExpanded(),
            )

            CategorySelector(
                state = state,
                isReorderMode = editState,
                onAction = onAction,
                onAddCategoryClick = {
                    onAction(TaskAction.OnTaskCategorySheetOpened)
                    showCategoryAddSheet = true
                },
                onEditCategoriesClick = onEditCategories,
                isExpanded = windowSizeClass.isExpanded(),
                onReorderModeChange = { editState = it },
            )

            if (!windowSizeClass.isExpanded()) {
                CompactTasksView(
                    state = state,
                    isReorderMode = editState,
                    onAction = onAction,
                    onEditTask = { showTaskUpsertSheet = ShowTaskUpsertSheet.Edit(it) },
                    isCompact = windowSizeClass.isCompact(),
                    onShowSubTasksSheet = {
                        if (isPlusUser) {
                            showSubTaskUpsertSheet = it
                        } else {
                            showSubTasksFeaturePreview = true
                        }
                    },
                )
            } else {
                ExpandedTasksView(
                    state = state,
                    onAction = onAction,
                    onEditTask = { showTaskUpsertSheet = ShowTaskUpsertSheet.Edit(it) },
                    onShowSubTasksSheet = {
                        if (isPlusUser) {
                            showSubTaskUpsertSheet = it
                        } else {
                            showSubTasksFeaturePreview = true
                        }
                    },
                )
            }
        }

        val isFabVisible = state.currentCategory != null && !editState
        AnimatedVisibility(
            visible = isFabVisible,
            enter =
                fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                    scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
            exit =
                fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                    scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).navigationBarsPadding(),
        ) {
            MediumFloatingActionButton(
                onClick = { showTaskUpsertSheet = ShowTaskUpsertSheet.Add },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add),
                        contentDescription = null,
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                    )
                    AnimatedVisibility(
                        visible =
                            state.tasks[state.currentCategory].isNullOrEmpty() ||
                                windowSizeClass.isExpanded(),
                        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                    ) {
                        Text(
                            text = stringResource(Res.string.add_task),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            DeleteTasksDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    onAction(TaskAction.DeleteTasks)
                    showDeleteDialog = false
                },
            )
        }

        if (showCategoryAddSheet) {
            CategoryUpsertSheet(
                onDismiss = {
                    onAction(TaskAction.OnTaskCategorySheetDismissed)
                    showCategoryAddSheet = false
                },
                category = Category(name = "", color = CategoryColors.GRAY.color),
                onUpsertCategory = {
                    onAction(TaskAction.AddCategory(it))
                    onAction(TaskAction.OnTaskCategorySheetDismissed)
                    showCategoryAddSheet = false
                },
            )
        }

        showTaskUpsertSheet?.let { sheet ->
            if (state.currentCategory == null) return@let

            LaunchedEffect(sheet) { onAction(TaskAction.OnTaskSheetOpened) }

            TaskUpsertSheet(
                task =
                    when (sheet) {
                        Add ->
                            Task(
                                categoryId = state.currentCategory.id,
                                title = "",
                                index = state.tasks[state.currentCategory]?.size ?: 0,
                                status = false,
                                reminder = null,
                            )
                        is Edit -> sheet.task
                    },
                categories = state.tasks.keys.toList(),
                onDismissRequest = {
                    onAction(TaskAction.OnTaskSheetDismissed)
                    showTaskUpsertSheet = null
                },
                isEditSheet = sheet is ShowTaskUpsertSheet.Edit,
                is24Hr = state.is24Hour,
                onUpsert = {
                    onAction(TaskAction.UpsertTask(it))
                    onAction(TaskAction.OnTaskSheetDismissed)
                },
                onDelete = {
                    when (sheet) {
                        is Edit -> onAction(TaskAction.DeleteTask(sheet.task))
                        else -> {}
                    }
                    onAction(TaskAction.OnTaskSheetDismissed)
                },
            )
        }

        showSubTaskUpsertSheet?.let { sheet ->
            LaunchedEffect(Unit) { onAction(TaskAction.OnSubTaskSheetOpened) }

            SubTaskUpsertSheet(
                subTask =
                    when (sheet) {
                        is Add -> SubTask(taskId = sheet.parentTask.id, title = "", status = false)
                        is Edit -> sheet.subTask
                    },
                isEditSheet = sheet is ShowSubTaskUpsertSheet.Edit,
                onDismissRequest = {
                    onAction(TaskAction.OnSubTaskSheetDismissed)
                    showSubTaskUpsertSheet = null
                },
                is24Hr = state.is24Hour,
                onUpsert = {
                    onAction(TaskAction.UpsertSubTask(it))
                    showSubTaskUpsertSheet = null
                },
                onDelete = {
                    when (sheet) {
                        is Edit -> onAction(TaskAction.DeleteSubTask(sheet.subTask))
                        else -> {}
                    }
                },
            )
        }

        if (showSubTasksFeaturePreview) {
            LaunchedEffect(Unit) { onAction(OnSubTaskPreview) }

            SubTasksFeaturePreview(
                is24Hr = state.is24Hour,
                onOpenPaywall = {
                    onOpenPaywall()
                    showSubTasksFeaturePreview = false
                },
                onDismissRequest = { showSubTasksFeaturePreview = false },
            )
        }
    }

@Composable
private fun TaskListTopBar(
    state: TaskState,
    scrollBehavior: TopAppBarScrollBehavior,
    isReorderMode: Boolean,
    onReorderToggle: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    isExpanded: Boolean,
) {
    LargeFlexibleTopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
        scrollBehavior = scrollBehavior,
        title = { Text(text = stringResource(Res.string.tasks), fontFamily = flexFontEmphasis()) },
        subtitle = {
            Text(
                text = "${state.completedTasks.size} " + stringResource(Res.string.items_completed),
                fontFamily = flexFontRounded(),
            )
        },
        actions = {
            val motionScheme = MaterialTheme.motionScheme
            AnimatedVisibility(
                visible = state.completedTasks.isNotEmpty(),
                enter = fadeIn(motionScheme.fastEffectsSpec()),
                exit = fadeOut(motionScheme.fastEffectsSpec()),
            ) {
                OutlinedIconButton(
                    onClick = onDeleteClick,
                    shapes =
                        IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.delete),
                        contentDescription = null,
                    )
                }
            }

            AnimatedVisibility(
                visible = state.tasks.values.isNotEmpty() && !isExpanded,
                enter = fadeIn(motionScheme.fastEffectsSpec()),
                exit = fadeOut(motionScheme.fastEffectsSpec()),
            ) {
                FilledTonalIconToggleButton(
                    checked = isReorderMode,
                    shapes =
                        IconToggleButtonShapes(
                            shape = CircleShape,
                            checkedShape = MaterialTheme.shapes.small,
                            pressedShape = MaterialTheme.shapes.extraSmall,
                        ),
                    onCheckedChange = onReorderToggle,
                    enabled = !state.tasks[state.currentCategory].isNullOrEmpty(),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.reorder),
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
private fun CategorySelector(
    state: TaskState,
    isReorderMode: Boolean,
    onAction: (TaskAction) -> Unit,
    onAddCategoryClick: () -> Unit,
    onEditCategoriesClick: () -> Unit,
    isExpanded: Boolean,
    onReorderModeChange: (Boolean) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    ) {
        if (!isExpanded) {
            items(state.tasks.keys.toList(), key = { it.id }) { category ->
                ToggleButton(
                    checked = category == state.currentCategory,
                    onCheckedChange = {
                        onAction(TaskAction.ChangeCategory(category))
                        onReorderModeChange(false)
                    },
                ) {
                    Text(text = category.name)
                }
            }
            item {
                Spacer(modifier = Modifier.width(4.dp))
                FilledTonalIconButton(onClick = onAddCategoryClick, enabled = !isReorderMode) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add),
                        contentDescription = "Add Category",
                    )
                }
                FilledTonalIconButton(onClick = onEditCategoriesClick, enabled = !isReorderMode) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.edit),
                        contentDescription = "Edit Categories",
                    )
                }
            }
        } else {
            item {
                FilledTonalButton(onClick = onAddCategoryClick, enabled = !isReorderMode) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add),
                        contentDescription = "Add Category",
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.add_category))
                }
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(onClick = onEditCategoriesClick, enabled = !isReorderMode) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.edit),
                        contentDescription = "Edit Categories",
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.edit_categories))
                }
            }
        }
    }
}

@Composable
private fun CompactTasksView(
    state: TaskState,
    isReorderMode: Boolean,
    onAction: (TaskAction) -> Unit,
    onEditTask: (Task) -> Unit,
    onShowSubTasksSheet: (ShowSubTaskUpsertSheet) -> Unit,
    isCompact: Boolean,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.padding(horizontal = if (isCompact) 0.dp else 16.dp),
    ) {
        val motionScheme = MaterialTheme.motionScheme
        AnimatedContent(
            targetState = state.currentCategory?.id,
            transitionSpec = {
                fadeIn(motionScheme.fastEffectsSpec()) togetherWith
                    fadeOut(motionScheme.fastEffectsSpec())
            },
        ) { categoryId ->
            val category = state.tasks.keys.firstOrNull { it.id == categoryId }
            if (category != null) {
                val lazyListState = rememberLazyListState()
                var reorderableTasks by
                    remember(state.tasks.values) {
                        mutableStateOf(
                            (state.tasks[category] ?: emptyList()).run {
                                if (state.reorderTasks) {
                                    filter { !it.task.status }
                                } else this
                            }
                        )
                    }
                val reorderableListState =
                    rememberReorderableLazyListState(lazyListState) { from, to ->
                        reorderableTasks =
                            reorderableTasks.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                    }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    itemsIndexed(items = reorderableTasks, key = { _, it -> it.task.id }) {
                        index,
                        taskWithSubTasks ->
                        ReorderableItem(reorderableListState, key = taskWithSubTasks.task.id) {
                            val cardShape =
                                when {
                                    reorderableTasks.size == 1 -> RoundedCornerShape(20.dp)
                                    index == 0 ->
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = 4.dp,
                                            bottomEnd = 4.dp,
                                        )

                                    index == reorderableTasks.size - 1 ->
                                        RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp,
                                            bottomStart = 20.dp,
                                            bottomEnd = 20.dp,
                                        )

                                    else -> RoundedCornerShape(4.dp)
                                }

                            TaskCard(
                                taskWithSubTasks = taskWithSubTasks,
                                dragState = isReorderMode,
                                reorderIcon = {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.drag_indicator),
                                        contentDescription = "Drag",
                                        modifier =
                                            Modifier.draggableHandle(
                                                onDragStopped = {
                                                    onAction(
                                                        TaskAction.ReorderTasks(
                                                            reorderableTasks.mapIndexed { i, t ->
                                                                i to t.task
                                                            }
                                                        )
                                                    )
                                                }
                                            ),
                                    )
                                },
                                is24Hr = state.is24Hour,
                                shape = cardShape,
                                onUpdateStatus = {
                                    when (it) {
                                        TaskStatus -> {
                                            if (!isReorderMode) {
                                                val updatedTask =
                                                    taskWithSubTasks.task.copy(
                                                        status = !taskWithSubTasks.task.status
                                                    )

                                                onAction(TaskAction.UpsertTask(updatedTask))
                                            }
                                        }

                                        is SubTaskStatus -> {
                                            onAction(
                                                TaskAction.UpsertSubTask(
                                                    it.subTask.copy(status = !it.subTask.status)
                                                )
                                            )
                                        }
                                    }
                                },
                                onEdit = {
                                    if (!isReorderMode && !taskWithSubTasks.task.status) {
                                        onEditTask(taskWithSubTasks.task)
                                    }
                                },
                                onShowSubTasksSheet = onShowSubTasksSheet,
                            )
                        }
                    }

                    if (state.reorderTasks) {
                        val completedTasks =
                            (state.tasks[category] ?: emptyList()).filter { it.task.status }

                        if (reorderableTasks.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                        itemsIndexed(
                            items = completedTasks,
                            key = { _, it -> "completed_task_${it.task.id}" },
                        ) { index, taskWithSubTasks ->
                            val cardShape =
                                when {
                                    completedTasks.size == 1 -> RoundedCornerShape(20.dp)
                                    index == 0 ->
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = 4.dp,
                                            bottomEnd = 4.dp,
                                        )

                                    index == completedTasks.size - 1 ->
                                        RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp,
                                            bottomStart = 20.dp,
                                            bottomEnd = 20.dp,
                                        )

                                    else -> RoundedCornerShape(4.dp)
                                }

                            TaskCard(
                                taskWithSubTasks = taskWithSubTasks,
                                dragState = false,
                                reorderIcon = {},
                                is24Hr = state.is24Hour,
                                shape = cardShape,
                                onUpdateStatus = {
                                    when (it) {
                                        TaskStatus -> {
                                            if (!isReorderMode) {
                                                val updatedTask =
                                                    taskWithSubTasks.task.copy(
                                                        status = !taskWithSubTasks.task.status
                                                    )

                                                onAction(TaskAction.UpsertTask(updatedTask))
                                            }
                                        }

                                        is SubTaskStatus -> {
                                            onAction(
                                                TaskAction.UpsertSubTask(
                                                    it.subTask.copy(status = !it.subTask.status)
                                                )
                                            )
                                        }
                                    }
                                },
                                onEdit = {},
                                onShowSubTasksSheet = onShowSubTasksSheet,
                            )
                        }
                    }

                    if (reorderableTasks.isEmpty()) {
                        item { Empty(modifier = Modifier.padding(top = 150.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedTasksView(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onEditTask: (Task) -> Unit,
    onShowSubTasksSheet: (ShowSubTaskUpsertSheet) -> Unit,
) {
    val tasksAndCategories = state.tasks.toList()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 350.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 60.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(tasksAndCategories, key = { it.first.id }) { (category, groups) ->
            val displayTasks = if (state.reorderTasks) groups.filter { !it.task.status } else groups
            var showReorderDialog by remember { mutableStateOf(false) }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(28.dp),
                modifier =
                    Modifier.widthIn(max = 350.dp)
                        .heightIn(max = 1000.dp)
                        .animateContentSize(
                            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                        ),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(end = 8.dp).weight(1f),
                            )

                            FilledTonalIconToggleButton(
                                checked = showReorderDialog,
                                onCheckedChange = { showReorderDialog = it },
                                enabled = displayTasks.size > 1,
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.reorder),
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    itemsIndexed(items = displayTasks, key = { _, it -> it.task.id }) {
                        index,
                        taskWithSubTasks ->
                        val cardShape =
                            when {
                                displayTasks.size == 1 -> RoundedCornerShape(20.dp)
                                index == 0 ->
                                    RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = 4.dp,
                                        bottomEnd = 4.dp,
                                    )

                                index == displayTasks.size - 1 ->
                                    RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 4.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = 20.dp,
                                    )

                                else -> RoundedCornerShape(4.dp)
                            }

                        TaskCard(
                            taskWithSubTasks = taskWithSubTasks,
                            dragState = false,
                            reorderIcon = {},
                            is24Hr = state.is24Hour,
                            shape = cardShape,
                            onUpdateStatus = {
                                when (it) {
                                    TaskStatus -> {
                                        val updatedTask =
                                            taskWithSubTasks.task.copy(
                                                status = !taskWithSubTasks.task.status
                                            )

                                        onAction(TaskAction.UpsertTask(updatedTask))
                                    }

                                    is SubTaskStatus -> {
                                        onAction(
                                            TaskAction.UpsertSubTask(
                                                it.subTask.copy(status = !it.subTask.status)
                                            )
                                        )
                                    }
                                }
                            },
                            onEdit = {
                                if (!taskWithSubTasks.task.status) {
                                    onEditTask(taskWithSubTasks.task)
                                }
                            },
                            onShowSubTasksSheet = onShowSubTasksSheet,
                        )
                    }

                    if (state.reorderTasks) {
                        val completedTasks = groups.filter { it.task.status }

                        if (completedTasks.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                        itemsIndexed(items = completedTasks, key = { _, it -> it.task.id }) {
                            index,
                            taskWithSubtasks ->
                            val cardShape =
                                when {
                                    completedTasks.size == 1 -> RoundedCornerShape(20.dp)
                                    index == 0 ->
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = 4.dp,
                                            bottomEnd = 4.dp,
                                        )

                                    index == completedTasks.size - 1 ->
                                        RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp,
                                            bottomStart = 20.dp,
                                            bottomEnd = 20.dp,
                                        )

                                    else -> RoundedCornerShape(4.dp)
                                }

                            TaskCard(
                                taskWithSubTasks = taskWithSubtasks,
                                dragState = false,
                                reorderIcon = {},
                                is24Hr = state.is24Hour,
                                shape = cardShape,
                                onUpdateStatus = {
                                    when (it) {
                                        TaskStatus -> {
                                            val updatedTask =
                                                taskWithSubtasks.task.copy(
                                                    status = !taskWithSubtasks.task.status
                                                )

                                            onAction(TaskAction.UpsertTask(updatedTask))
                                        }

                                        is SubTaskStatus -> {
                                            onAction(
                                                TaskAction.UpsertSubTask(
                                                    it.subTask.copy(status = !it.subTask.status)
                                                )
                                            )
                                        }
                                    }
                                },
                                onEdit = {
                                    if (!taskWithSubtasks.task.status) {
                                        onEditTask(taskWithSubtasks.task)
                                    }
                                },
                                onShowSubTasksSheet = onShowSubTasksSheet,
                            )
                        }
                    }
                    if (groups.isEmpty()) {
                        item { Empty(modifier = Modifier.padding(32.dp)) }
                    }
                }
            }

            if (showReorderDialog) {
                GritDialog(onDismissRequest = { showReorderDialog = false }, padding = 0.dp) {
                    var reorderableTasks = remember { displayTasks }

                    val listState = rememberLazyListState()
                    val reorderableListState =
                        rememberReorderableLazyListState(listState) { from, to ->
                            reorderableTasks =
                                reorderableTasks.toMutableList().apply {
                                    add(to.index, removeAt(from.index))
                                }

                            onAction(
                                TaskAction.ReorderTasks(
                                    reorderableTasks.mapIndexed { index, taskWithSubtasks ->
                                        index to taskWithSubtasks.task
                                    }
                                )
                            )
                        }

                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .heightIn(max = 600.dp)
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
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
                                imageVector = vectorResource(Res.drawable.reorder),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        Text(
                            text = stringResource(Res.string.reorder_tasks),
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = flexFontEmphasis()
                                ),
                        )

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                        ) {
                            itemsIndexed(items = reorderableTasks, key = { _, it -> it.task.id }) {
                                index,
                                taskWithSubTasks ->
                                ReorderableItem(
                                    reorderableListState,
                                    key = taskWithSubTasks.task.id,
                                ) {
                                    val shape =
                                        when {
                                            reorderableTasks.size == 1 -> detachedItemShape()
                                            index == 0 -> leadingItemShape()
                                            index == reorderableTasks.size - 1 -> endItemShape()
                                            else -> middleItemShape()
                                        }

                                    ListItem(
                                        modifier = Modifier.clip(shape),
                                        colors =
                                            ListItemDefaults.colors(
                                                containerColor =
                                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                            ),
                                        headlineContent = {
                                            Text(
                                                text = taskWithSubTasks.task.title,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        },
                                        trailingContent = {
                                            Icon(
                                                imageVector =
                                                    vectorResource(Res.drawable.drag_indicator),
                                                contentDescription = null,
                                                modifier =
                                                    Modifier.padding(horizontal = 8.dp)
                                                        .draggableHandle(),
                                            )
                                        },
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

@Composable
private fun DeleteTasksDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    GritDialog(onDismissRequest = onDismiss) {
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
                    imageVector = vectorResource(Res.drawable.warning),
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.delete),
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontEmphasis()),
            )
            Text(
                text = stringResource(Res.string.delete_tasks),
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = onDismiss,
                    shapes =
                        ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Text(stringResource(Res.string.cancel))
                }

                TextButton(
                    onClick = onConfirm,
                    shapes =
                        ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.small,
                        ),
                ) {
                    Text(stringResource(Res.string.delete))
                }
            }
        }
    }
}
