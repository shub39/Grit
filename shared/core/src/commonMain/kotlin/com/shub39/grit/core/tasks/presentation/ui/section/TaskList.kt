package com.shub39.grit.core.tasks.presentation.ui.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.shared_ui.Empty
import com.shub39.grit.core.shared_ui.GritDialog
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.CategoryColors
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.tasks.presentation.ui.component.CategoryUpsertSheet
import com.shub39.grit.core.tasks.presentation.ui.component.TaskCard
import com.shub39.grit.core.tasks.presentation.ui.component.TaskUpsertSheet
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.add_category
import grit.shared.core.generated.resources.add_task
import grit.shared.core.generated.resources.cancel
import grit.shared.core.generated.resources.delete
import grit.shared.core.generated.resources.delete_tasks
import grit.shared.core.generated.resources.drag_indicator
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_categories
import grit.shared.core.generated.resources.items_completed
import grit.shared.core.generated.resources.reorder
import grit.shared.core.generated.resources.reorder_tasks
import grit.shared.core.generated.resources.tasks
import grit.shared.core.generated.resources.warning
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onEditCategories: () -> Unit
) = PageFill {
    val windowSizeClass = LocalWindowSizeClass.current

    var showTaskAddSheet by remember { mutableStateOf(false) }
    var showCategoryAddSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editState by remember { mutableStateOf(false) }
    var editTask: Task? by remember { mutableStateOf(null) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        TaskListTopBar(
            state = state,
            scrollBehavior = scrollBehavior,
            isReorderMode = editState,
            onReorderToggle = { editState = it },
            onDeleteClick = { showDeleteDialog = true },
            isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
        )

        CategorySelector(
            state = state,
            isReorderMode = editState,
            onAction = onAction,
            onAddCategoryClick = { showCategoryAddSheet = true },
            onEditCategoriesClick = onEditCategories,
            isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded,
            onReorderModeChange = { editState = it }
        )

        if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
            CompactTasksView(
                state = state,
                isReorderMode = editState,
                onAction = onAction,
                onEditTask = { editTask = it },
                isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            )
        } else {
            ExpandedTasksView(
                state = state,
                onAction = onAction,
                onEditTask = { editTask = it }
            )
        }
    }

    MediumFloatingActionButton(
        onClick = { showTaskAddSheet = true },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .animateFloatingActionButton(
                visible = state.currentCategory != null,
                alignment = Alignment.BottomEnd,
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.add),
                contentDescription = null,
                modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
            )
            AnimatedVisibility(
                visible = state.tasks[state.currentCategory].isNullOrEmpty() || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
            ) {
                Text(
                    text = stringResource(Res.string.add_task),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTasksDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onAction(TaskAction.DeleteTasks)
                showDeleteDialog = false
            }
        )
    }

    if (showCategoryAddSheet) {
        CategoryUpsertSheet(
            onDismiss = { showCategoryAddSheet = false },
            category = Category(
                name = "",
                color = CategoryColors.GRAY.color
            ),
            onUpsertCategory = {
                onAction(TaskAction.AddCategory(it))
                showCategoryAddSheet = false
            }
        )
    }

    if (editTask != null) {
        TaskUpsertSheet(
            task = editTask!!,
            categories = state.tasks.keys.toList(),
            onDismissRequest = { editTask = null },
            isEditSheet = true,
            is24Hr = state.is24Hour,
            onUpsert = { onAction(TaskAction.UpsertTask(it)) },
            onDelete = {
                editTask?.let { onAction(TaskAction.DeleteTask(it)) }
                editTask = null
            }
        )
    }

    if (showTaskAddSheet && state.currentCategory != null) {
        TaskUpsertSheet(
            task = Task(
                categoryId = state.currentCategory.id,
                title = "",
                index = state.tasks[state.currentCategory]?.size ?: 0,
                status = false,
                reminder = null
            ),
            is24Hr = state.is24Hour,
            categories = state.tasks.keys.toList(),
            onDismissRequest = { showTaskAddSheet = false },
            onUpsert = { onAction(TaskAction.UpsertTask(it)) },
            onDelete = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TaskListTopBar(
    state: TaskState,
    scrollBehavior: TopAppBarScrollBehavior,
    isReorderMode: Boolean,
    onReorderToggle: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    isExpanded: Boolean
) {
    LargeFlexibleTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior,
        title = { Text(text = stringResource(Res.string.tasks)) },
        subtitle = {
            Text(
                text = "${state.completedTasks.size} " + stringResource(Res.string.items_completed)
            )
        },
        actions = {
            AnimatedVisibility(visible = state.completedTasks.isNotEmpty()) {
                OutlinedIconButton(
                    onClick = onDeleteClick,
                    shapes = IconButtonShapes(
                        shape = CircleShape,
                        pressedShape = MaterialTheme.shapes.small
                    )
                ) {
                    Icon(imageVector = vectorResource(Res.drawable.delete), contentDescription = null)
                }
            }

            AnimatedVisibility(visible = state.tasks.values.isNotEmpty() && !isExpanded) {
                FilledTonalIconToggleButton(
                    checked = isReorderMode,
                    shapes = IconToggleButtonShapes(
                        shape = CircleShape,
                        checkedShape = MaterialTheme.shapes.small,
                        pressedShape = MaterialTheme.shapes.extraSmall,
                    ),
                    onCheckedChange = onReorderToggle,
                    enabled = !state.tasks[state.currentCategory].isNullOrEmpty()
                ) {
                    Icon(imageVector = vectorResource(Res.drawable.reorder), contentDescription = null)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategorySelector(
    state: TaskState,
    isReorderMode: Boolean,
    onAction: (TaskAction) -> Unit,
    onAddCategoryClick: () -> Unit,
    onEditCategoriesClick: () -> Unit,
    isExpanded: Boolean,
    onReorderModeChange: (Boolean) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        if (!isExpanded) {
            items(state.tasks.keys.toList(), key = { it.id }) { category ->
                ToggleButton(
                    checked = category == state.currentCategory,
                    onCheckedChange = {
                        onAction(TaskAction.ChangeCategory(category))
                        onReorderModeChange(false)
                    }
                ) {
                    Text(text = category.name)
                }
            }
            item {
                FilledTonalIconButton(onClick = onAddCategoryClick, enabled = !isReorderMode) {
                    Icon(imageVector = vectorResource(Res.drawable.add), contentDescription = "Add Category")
                }
                FilledTonalIconButton(onClick = onEditCategoriesClick, enabled = !isReorderMode) {
                    Icon(imageVector = vectorResource(Res.drawable.edit), contentDescription = "Edit Categories")
                }
            }
        } else {
            item {
                FilledTonalButton(onClick = onAddCategoryClick, enabled = !isReorderMode) {
                    Icon(imageVector = vectorResource(Res.drawable.add), contentDescription = "Add Category")
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.add_category))
                }
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(onClick = onEditCategoriesClick, enabled = !isReorderMode) {
                    Icon(imageVector = vectorResource(Res.drawable.edit), contentDescription = "Edit Categories")
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.edit_categories))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactTasksView(
    state: TaskState,
    isReorderMode: Boolean,
    onAction: (TaskAction) -> Unit,
    onEditTask: (Task) -> Unit,
    isCompact: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.padding(horizontal = if (isCompact) 0.dp else 16.dp)
    ) {
        AnimatedContent(targetState = state.currentCategory?.id) { categoryId ->
            val category = state.tasks.keys.firstOrNull { it.id == categoryId }
            if (category != null) {
                val lazyListState = rememberLazyListState()
                var reorderableTasks by remember(state.tasks.values) {
                    mutableStateOf(
                        (state.tasks[category] ?: emptyList()).run {
                            if (state.reorderTasks) {
                                filter { !it.status }
                            } else this
                        }
                    )
                }
                val reorderableListState =
                    rememberReorderableLazyListState(lazyListState) { from, to ->
                        reorderableTasks = reorderableTasks.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = reorderableTasks,
                        key = { _, it -> it.id }
                    ) { _, task ->
                        ReorderableItem(reorderableListState, key = task.id) {
                            val cardCorners by animateDpAsState(targetValue = if (it) 28.dp else 20.dp)
                            TaskCard(
                                task = task,
                                dragState = isReorderMode,
                                reorderIcon = {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.drag_indicator),
                                        contentDescription = "Drag",
                                        modifier = Modifier.draggableHandle(onDragStopped = {
                                            onAction(TaskAction.ReorderTasks(reorderableTasks.mapIndexed { i, t -> i to t }))
                                        })
                                    )
                                },
                                is24Hr = state.is24Hour,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(cardCorners))
                                    .combinedClickable(
                                        onClick = {
                                            if (!isReorderMode) {
                                                val updatedTask =
                                                    task.copy(status = !task.status)

                                                onAction(TaskAction.UpsertTask(updatedTask))
                                            }
                                        },
                                        onLongClick = {
                                            if (!isReorderMode && !task.status) {
                                                onEditTask(task)
                                            }
                                        },

                                    )
                            )
                        }
                    }

                    if (state.reorderTasks) {
                        itemsIndexed(
                            items = (state.tasks[category] ?: emptyList()).filter { it.status },
                            key = { _, it -> "completed_task_${it.id}" }
                        ) { _, task ->
                            TaskCard(
                                task = task,
                                dragState = false,
                                reorderIcon = {},
                                is24Hr = state.is24Hour,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28))
                                    .combinedClickable(
                                        onClick = {
                                            if (!isReorderMode) {
                                                val updatedTask =
                                                    task.copy(status = !task.status)

                                                onAction(TaskAction.UpsertTask(updatedTask))
                                            }
                                        },
                                        onLongClick = {}
                                    )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpandedTasksView(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onEditTask: (Task) -> Unit
) {
    val tasksAndCategories = state.tasks.toList()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 350.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 60.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(tasksAndCategories, key = { it.first.id }) { (category, tasks) ->
            var showReorderDialog by remember { mutableStateOf(false) }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .widthIn(max = 350.dp)
                    .heightIn(max = 1000.dp)
                    .animateContentSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(end = 8.dp).weight(1f)
                            )

                            FilledTonalIconToggleButton(
                                checked = showReorderDialog,
                                onCheckedChange = { showReorderDialog = it },
                                enabled = tasks.size > 1
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.reorder),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    items(
                        items = tasks.run {
                            if (state.reorderTasks) {
                                filter { !it.status }
                            } else this
                        },
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            dragState = false,
                            reorderIcon = {},
                            is24Hr = state.is24Hour,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .combinedClickable(
                                    onClick = {
                                        val updatedTask = task.copy(status = !task.status)
                                        onAction(TaskAction.UpsertTask(updatedTask))
                                    },
                                    onLongClick = { if (!task.status) onEditTask(task) }
                                )
                        )
                    }
                    if (state.reorderTasks) {
                        items(
                            items = tasks.filter { it.status },
                            key = { it.id }
                        ) { task ->
                            TaskCard(
                                task = task,
                                dragState = false,
                                reorderIcon = {},
                                is24Hr = state.is24Hour,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .combinedClickable(
                                        onClick = {
                                            val updatedTask = task.copy(status = !task.status)
                                            onAction(TaskAction.UpsertTask(updatedTask))
                                        },
                                        onLongClick = { if (!task.status) onEditTask(task) }
                                    )
                            )
                        }
                    }
                    if (tasks.isEmpty()) {
                        item { Empty(modifier = Modifier.padding(32.dp)) }
                    }
                }
            }

            if (showReorderDialog) {
                GritDialog(
                    onDismissRequest = { showReorderDialog = false },
                    padding = 0.dp
                ) {
                    var reorderableTasks = remember { tasks }

                    val listState = rememberLazyListState()
                    val reorderableListState =
                        rememberReorderableLazyListState(listState) { from, to ->
                            reorderableTasks = reorderableTasks.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }

                            onAction(TaskAction.ReorderTasks(reorderableTasks.mapIndexed { index, task -> index to task }))
                        }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.reorder),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = stringResource(Res.string.reorder_tasks),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(
                                items = reorderableTasks.run {
                                    if (state.reorderTasks) {
                                        filter { !it.status }
                                    } else this
                                },
                                key = { it.id }
                            ) { task ->
                                ReorderableItem(reorderableListState, key = task.id) {
                                    ListItem(
                                        modifier = Modifier.clip(MaterialTheme.shapes.medium),
                                        colors = ListItemDefaults.colors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                        ),
                                        headlineContent = {
                                            Text(
                                                text = task.title,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        trailingContent = {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.drag_indicator),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .draggableHandle(),
                                            )
                                        }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DeleteTasksDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    GritDialog(onDismissRequest = onDismiss) {
        Icon(
            imageVector = vectorResource(Res.drawable.warning),
            contentDescription = "Warning",
        )
        Text(
            text = stringResource(Res.string.delete),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(Res.string.delete_tasks),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onDismiss,
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                )
            ) {
                Text(stringResource(Res.string.cancel))
            }

            TextButton(
                onClick = onConfirm,
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                )
            ) {
                Text(stringResource(Res.string.delete))
            }
        }
    }
}