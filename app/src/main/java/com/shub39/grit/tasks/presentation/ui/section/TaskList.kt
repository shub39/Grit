package com.shub39.grit.tasks.presentation.ui.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.Empty
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.presentation.TaskPageAction
import com.shub39.grit.tasks.presentation.TaskPageState
import com.shub39.grit.tasks.presentation.ui.component.TaskCard
import com.shub39.grit.tasks.presentation.ui.component.TaskUpsertSheet
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit,
    onNavigateToEditCategories: () -> Unit
) = PageFill {
    var showTaskAddSheet by remember { mutableStateOf(false) }
    var showCategoryAddSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editState by remember { mutableStateOf(false) }
    var editTask: Task? by remember { mutableStateOf(null) }

    val lazyListState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex <= 0
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LargeFlexibleTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            scrollBehavior = scrollBehavior,
            title = { Text(text = stringResource(R.string.tasks)) },
            subtitle = {
                Text(
                    text = "${state.completedTasks.size} " + stringResource(R.string.items_completed)
                )
            },
            actions = {
                AnimatedVisibility(
                    visible = state.completedTasks.isNotEmpty()
                ) {
                    OutlinedIconButton(
                        onClick = { showDeleteDialog = true },
                        shapes = IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = MaterialTheme.shapes.small
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                    }
                }

                AnimatedVisibility(
                    visible = state.tasks.values.isNotEmpty()
                ) {
                    FilledTonalIconToggleButton(
                        checked = editState,
                        shapes = IconToggleButtonShapes(
                            shape = CircleShape,
                            checkedShape = MaterialTheme.shapes.small,
                            pressedShape = MaterialTheme.shapes.extraSmall,
                        ),
                        onCheckedChange = { editState = it },
                        enabled = !state.tasks[state.currentCategory].isNullOrEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Reorder,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(state.tasks.keys.toList(), key = { it.id }) { category ->
                ToggleButton(
                    checked = category == state.currentCategory,
                    onCheckedChange = {
                        onAction(TaskPageAction.ChangeCategory(category))
                        editState = false
                    }
                ) {
                    Text(text = category.name)
                }
            }

            item {
                FilledTonalIconButton(
                    onClick = { showCategoryAddSheet = true },
                    enabled = !editState
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Category"
                    )
                }

                FilledTonalIconButton(
                    onClick = onNavigateToEditCategories,
                    enabled = !editState
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Categories"
                    )
                }
            }
        }

        AnimatedContent(
            targetState = state.currentCategory?.id
        ) { categoryId ->
            val category = state.tasks.keys.firstOrNull { it.id == categoryId }

            if (category != null) {
                var reorderableTasks by remember(category.id, state.tasks.values) {
                    mutableStateOf(
                        state.tasks[category] ?: emptyList()
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
                    state = lazyListState
                ) {
                    itemsIndexed(reorderableTasks, key = { _, it -> it.id }) { index, task ->
                        ReorderableItem(reorderableListState, key = task.id) {
                            val cardCorners by animateDpAsState(
                                targetValue = if (it) 16.dp else 30.dp
                            )

                            TaskCard(
                                task = task,
                                dragState = editState,
                                reorderIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.DragIndicator,
                                        contentDescription = "Drag",
                                        modifier = Modifier.draggableHandle(
                                            onDragStopped = {
                                                onAction(
                                                    TaskPageAction.ReorderTasks(
                                                        reorderableTasks.mapIndexed { index, task -> index to task })
                                                )
                                            }
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(cardCorners),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                                    .combinedClickable(
                                        // change status on click
                                        onClick = {
                                            if (!editState) {
                                                onAction(
                                                    TaskPageAction.UpsertTask(
                                                        task.copy(
                                                            status = !task.status,
                                                            reminder = if (!task.status) null else task.reminder
                                                        )
                                                    )
                                                )

                                                if (!task.status && state.reorderTasks) {
                                                    reorderableTasks =
                                                        reorderableTasks.toMutableList().apply {
                                                            add(
                                                                reorderableTasks.size - 1,
                                                                removeAt(index)
                                                            )
                                                        }

                                                    onAction(
                                                        TaskPageAction.ReorderTasks(
                                                            reorderableTasks.mapIndexed { index, task -> index to task })
                                                    )
                                                }
                                            }
                                        },
                                        // edit on click and hold
                                        onLongClick = {
                                            if (!editState && !task.status) {
                                                editTask = task
                                            }
                                        }
                                    )
                            )
                        }
                    }

                    item {
                        if (reorderableTasks.isEmpty()) {
                            Empty()
                        } else {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        }
    }

    MediumFloatingActionButton(
        onClick = { showTaskAddSheet = true },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .animateFloatingActionButton(
                visible = state.currentCategory != null && fabVisible,
                alignment = Alignment.BottomEnd,
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
            )

            AnimatedVisibility(
                visible = state.tasks[state.currentCategory].isNullOrEmpty()
            ) {
                Text(
                    text = stringResource(R.string.add_task),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    // delete dialog
    if (showDeleteDialog) {
        GritDialog(
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = "Warning",
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = stringResource(id = R.string.delete_tasks),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    onAction(TaskPageAction.DeleteTasks)
                    showDeleteDialog = false
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }

    if (showCategoryAddSheet) {
        var name by remember { mutableStateOf("") }

        GritBottomSheet(
            onDismissRequest = {
                showCategoryAddSheet = false
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add"
            )

            Text(
                text = stringResource(R.string.add_category),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    onAction(
                        TaskPageAction.AddCategory(
                            Category(
                                name = name,
                                color = CategoryColors.GRAY.color
                            )
                        )
                    )
                    showCategoryAddSheet = false
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && name.length <= 20
            ) {
                Text(text = stringResource(R.string.done))
            }
        }
    }

    //edit sheet
    if (editTask != null) {
        TaskUpsertSheet(
            task = editTask!!,
            categories = state.tasks.keys.toList(),
            onDismissRequest = { editTask = null },
            edit = true,
            is24Hr = state.is24Hour,
            onUpsert = {
                onAction(TaskPageAction.UpsertTask(it))
            }
        )
    }

    // add sheet
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
            onUpsert = { onAction(TaskPageAction.UpsertTask(it)) },
        )
    }
}