package com.shub39.grit.tasks.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.Empty
import com.shub39.grit.core.presentation.components.GritBottomSheet
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.presentation.TaskPageAction
import com.shub39.grit.tasks.presentation.TaskPageState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit,
    onNavigateToEditCategories: () -> Unit
) {
    var showTaskAddSheet by remember { mutableStateOf(false) }
    var showCategoryAddSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editState by remember { mutableStateOf(false) }

    var tasks: List<Task> by remember { mutableStateOf(emptyList()) }
    val lazyListState = rememberLazyListState()
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            tasks = tasks.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.tasks)) },
                subtitle = {
                    Text(
                        text = "${state.completedTasks.size} " + stringResource(R.string.items_completed)
                    )
                },
                actions = {
                    Row {
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
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = tasks.isNotEmpty()
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
                                    painter = painterResource(R.drawable.baseline_reorder_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                items(state.tasks.keys.toList(), key = { it.id }) { category ->
                    val chipCorner by animateDpAsState(
                        targetValue = if (category == state.currentCategory) 20.dp else 5.dp
                    )

                    FilterChip(
                        selected = category == state.currentCategory,
                        onClick = {
                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                            onAction(TaskPageAction.ChangeCategory(category))
                            editState = false
                        },
                        shape = RoundedCornerShape(chipCorner),
                        label = { Text(category.name) }
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { showCategoryAddSheet = true },
                            enabled = !editState,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = AssistChipDefaults.assistChipBorder(false),
                            label = {
                                Icon(
                                    painter = painterResource(R.drawable.round_add_24),
                                    contentDescription = "Add Category"
                                )
                            }
                        )

                        AssistChip(
                            onClick = onNavigateToEditCategories,
                            enabled = !editState,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = AssistChipDefaults.assistChipBorder(false),
                            label = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_edit_square_24),
                                    contentDescription = "Edit Categories"
                                )
                            }
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = state.currentCategory
            ) { category ->
                if (category != null) {
                    tasks = state.tasks[category] ?: emptyList()

                    DisposableEffect(Unit) {
                        onDispose { onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task })) }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(tasks, key = { _, it -> it.id }) { index, task ->
                            ReorderableItem(reorderableListState, key = task.id) {
                                val cardCorners by animateDpAsState(
                                    targetValue = if (it) 100.dp else 16.dp
                                )

                                TaskCard(
                                    task = task,
                                    onStatusChange = { updatedTask ->
                                        onAction(TaskPageAction.UpdateTaskStatus(updatedTask))

                                        if (updatedTask.status) {
                                            tasks = tasks.toMutableList().apply {
                                                add(tasks.size - 1, removeAt(index))
                                            }

                                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                                        }
                                    },
                                    dragState = editState,
                                    reorderIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                            contentDescription = "Drag",
                                            modifier = Modifier.draggableHandle()
                                        )
                                    },
                                    shape = RoundedCornerShape(cardCorners),
                                    modifier = Modifier
                                )
                            }
                        }

                        item {
                            if (tasks.isEmpty()) {
                                Empty()
                            } else {
                                Spacer(modifier = Modifier.padding(60.dp))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { showTaskAddSheet = true },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_add_24),
                    contentDescription = null
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
    }

    // delete dialog
    if (showDeleteDialog) {
        GritDialog(
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Icon(
                painter = painterResource(R.drawable.round_warning_24),
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
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = { showCategoryAddSheet = false }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
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
                    .focusRequester(focusRequester)
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

    // add dialog
    if (showTaskAddSheet) {
        var newTask by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = { showTaskAddSheet = false }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )

            Text(
                text = stringResource(id = R.string.add_task),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        newTask.plus("\n")
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Button(
                onClick = {
                    showTaskAddSheet = false

                    if (state.currentCategory != null) {
                        onAction(
                            TaskPageAction.AddTask(
                                Task(
                                    categoryId = state.currentCategory.id,
                                    title = newTask,
                                    status = false,
                                    index = state.tasks[state.currentCategory]?.size ?: 0
                                )
                            )
                        )
                    }
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newTask.isNotEmpty() && newTask.length <= 100
            ) {
                Text(
                    text = stringResource(id = R.string.add_task),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}