package com.shub39.grit.tasks.presentation.task_page

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.Empty
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.presentation.component.TaskCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit,
) {
    var showTaskAddDialog by remember { mutableStateOf(false) }
    var showCategoryAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editState by remember { mutableStateOf(false) }

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
                title = {
                    Text(
                        text = stringResource(R.string.tasks)
                    )
                },
                actions = {
                    Row {
                        AnimatedVisibility(
                            visible = state.completedTasks.isNotEmpty()
                        ) {
                            IconButton(
                                onClick = { showDeleteDialog = true },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }

                        IconButton(
                            onClick = { editState = !editState },
                            colors = if (editState) {
                                IconButtonDefaults.filledIconButtonColors()
                            } else {
                                IconButtonDefaults.iconButtonColors()
                            },
                            enabled = !state.tasks[state.currentCategory].isNullOrEmpty()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_reorder_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                items(state.tasks.keys.toList(), key = { it.id }) { category ->
                    FilterChip(
                        selected = category == state.currentCategory,
                        onClick = {
                            onAction(TaskPageAction.ChangeCategory(category))
                            editState = false
                        },
                        label = { Text(category.name) }
                    )
                }

                item {
                    AnimatedVisibility(
                        visible = !editState
                    ) {
                        AssistChip(
                            onClick = { showCategoryAddDialog = true },
                            label = {
                                Icon(
                                    painter = painterResource(R.drawable.round_add_24),
                                    contentDescription = null
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
                    var tasks by remember(state.tasks) {
                        mutableStateOf(
                            state.tasks[category] ?: emptyList()
                        )
                    }

                    val lazyListState = rememberLazyListState()
                    val reorderableListState =
                        rememberReorderableLazyListState(lazyListState) { _, _ -> }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(tasks, key = { _, it -> it.id }) { index, task ->
                            ReorderableItem(reorderableListState, key = { task.id }) {
                                TaskCard(
                                    task = task,
                                    onStatusChange = { updatedTask ->
                                        if (updatedTask.status) {
                                            tasks = tasks.toMutableList().apply {
                                                add(tasks.size - 1, removeAt(index))
                                            }

                                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                                        }

                                        onAction(TaskPageAction.UpdateTaskStatus(updatedTask))
                                    },
                                    dragState = editState,
                                    moveUp = {
                                        if (index > 0) {
                                            tasks = tasks.toMutableList().apply {
                                                add(index - 1, removeAt(index))
                                            }

                                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                                        }
                                    },
                                    moveDown = {
                                        if (index < tasks.size - 1) {
                                            tasks = tasks.toMutableList().apply {
                                                add(index + 1, removeAt(index))
                                            }

                                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                                        }
                                    }
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
            onClick = { showTaskAddDialog = true }
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
                contentDescription = null,
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
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }

    if (showCategoryAddDialog) {
        var name by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritDialog(
            onDismissRequest = { showCategoryAddDialog = false }
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
                label = { Text(text = stringResource(id = R.string.add_category)) }
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
                    showCategoryAddDialog = false
                },
                enabled = name.isNotBlank() && name.length <= 20
            ) {
                Text(text = stringResource(R.string.done))
            }
        }
    }

    // add dialog
    if (showTaskAddDialog) {
        var newTask by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritDialog(
            onDismissRequest = { showTaskAddDialog = false }
        ) {
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
                modifier = Modifier.focusRequester(focusRequester),
                label = { Text(text = stringResource(id = R.string.add_task)) }
            )

            Button(
                onClick = {
                    showTaskAddDialog = false

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

@Preview(
    showSystemUi = true, showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TaskPagePreview() {
    GritTheme {
        Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                TaskPage(
                    state = TaskPageState(
                        tasks = (0L..10L).associate { category ->
                            Category(
                                id = category,
                                name = "Category: $category",
                                color = "gray"
                            ) to (0L..10L).map {
                                Task(
                                    id = it,
                                    categoryId = category,
                                    title = "$category $it",
                                    status = it % 2L == 0L
                                )
                            }
                        }
                    ),
                    onAction = {},
                )
            }
        }
    }
}