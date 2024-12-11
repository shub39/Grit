package com.shub39.grit.tasks.presentation.task_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.shub39.grit.core.presentation.GritTheme
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.presentation.component.TaskCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(
    state: TaskPageState,
    action: (TaskPageAction) -> Unit,
    onSettingsClick: () -> Unit
) {
    // dialog controllers
    var showTaskAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // remembered states and scopes
    val addState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    // animated size of the pull to refresh button
    val addSize by animateDpAsState(
        targetValue = if (addState.distanceFraction != 0f) 64.dp else 0.dp,
        label = "addSize"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
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
                                onClick = { action(TaskPageAction.DeleteTasks) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }

//                        IconButton(
//                            onClick = {}
//                        ) { }

                        IconButton(
                            onClick = onSettingsClick
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_settings_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            // Using pull to refresh as an interactive indicator to add tasks
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = {
                    coroutineScope.launch {
                        addState.animateToHidden()
                    }
                    showTaskAddDialog = true
                },
                state = addState,
                modifier = Modifier.fillMaxSize(),
                indicator = {}
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize(),
                ) {
                    // pull to refresh button
                    item {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(addSize / 4)
                                .height(addSize)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_add_24),
                                contentDescription = null
                            )
                        }
                    }

                    // tasks
                    if (state.currentCategory != null) {
                        val items = state.tasks[state.currentCategory] ?: emptyList()

                        items(items, key = { it.id }) {
                            TaskCard(
                                task = it,
                                onStatusChange = { updatedTask ->
                                    action(TaskPageAction.UpdateTaskStatus(updatedTask))
                                },
                            )
                        }
                    }

//                    // guide to using the app, needs work
//                    if (state.tasks.size == 1) {
//                        item {
//                            TasksGuide()
//                        }
//                    }
//
//                    // when no tasks
//                    if (state.tasks.isEmpty() && !showDeleteDialog && !showTaskAddDialog) {
//                        item {
//                            Empty()
//                        }
//                    }

                    // to leave some space in the bottom, idk looks good
                    item {
                        Spacer(modifier = Modifier.padding(60.dp))
                    }
                }
            }
        }
    }

    // delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Text(
                    text = stringResource(id = R.string.delete_tasks),
                    textAlign = TextAlign.Center
                )
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.round_warning_24),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        action(TaskPageAction.DeleteTasks)
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        )
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

        AlertDialog(
            onDismissRequest = { showTaskAddDialog = false },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = newTask,
                        onValueChange = { newTask = it },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier.focusRequester(focusRequester),
                        label = { Text(text = stringResource(id = R.string.add_task)) }
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showTaskAddDialog = false

                            if (state.currentCategory != null) {
                                action(
                                    TaskPageAction.AddTask(
                                        Task(
                                            categoryId = state.currentCategory.id,
                                            title = newTask,
                                            status = false
                                        )
                                    )
                                )
                            }

                            newTask = ""
                        },
                        enabled = newTask.isNotEmpty()
                    ) {
                        Text(
                            text = stringResource(id = R.string.add_task),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        )
    }
}

@Preview(
    showSystemUi = true, showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=673dp,height=841dp"
)
@Composable
private fun TaskPagePreview() {
    GritTheme {
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
            action = {},
            onSettingsClick = {}
        )
    }
}