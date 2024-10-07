package com.shub39.grit.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.shub39.grit.ui.component.Empty
import com.shub39.grit.viewModel.TaskListViewModel
import com.shub39.grit.database.task.Task
import com.shub39.grit.ui.component.TaskCard
import com.shub39.grit.ui.component.TasksGuide
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(
    viewModel: TaskListViewModel = koinViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    var sortedTasks = tasks.sortedBy { !it.priority }

    var showTaskAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var completedTasks by rememberSaveable { mutableIntStateOf(0) }
    val addState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val addSize by animateDpAsState(
        targetValue = if (addState.distanceFraction != 0f) 64.dp else 0.dp,
    )

    LaunchedEffect(tasks) {
        sortedTasks = tasks.sortedBy { !it.priority }
        for (task in tasks) {
            if (task.status) completedTasks++
        }
    }

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
                .padding(top = 16.dp)
                .fillMaxSize()
                .animateContentSize(),
        ) {
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

            items(sortedTasks, key = { it.id }) {
                TaskCard(
                    task = it,
                    onStatusChange = { updatedTask ->
                        viewModel.updateTaskStatus(updatedTask)
                        if (updatedTask.status) completedTasks++ else completedTasks--
                    },
                )
            }

            item {
                AnimatedVisibility(
                    visible = sortedTasks.size == 1,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    content = { TasksGuide() }
                )
            }

            item {
                AnimatedVisibility(
                    visible = sortedTasks.isEmpty() && !showDeleteDialog && !showTaskAddDialog,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    content = { Empty() }
                )
            }

            item {
                Spacer(modifier = Modifier.padding(60.dp))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = completedTasks != 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_delete_forever_24),
                            contentDescription = null
                        )
                    }
                }
            }

//            if (context.packageName.endsWith(".debug")) {
//                Spacer(modifier = Modifier.padding(8.dp))
//
//                FloatingActionButton(
//                    onClick = {
//                        var id = 0
//                        var priority = true
//
//                        while (id < 50) {
//
//                            viewModel.addTask(
//                                Task(
//                                    id++.toString(),
//                                    title = "Task $id",
//                                    priority = priority
//                                )
//                            )
//
//                            priority = !priority
//                        }
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.round_add_24),
//                        contentDescription = null
//                    )
//                }
//
//                Spacer(modifier = Modifier.padding(8.dp))
//
//                FloatingActionButton(
//                    onClick = {
//                        viewModel.deleteTasks(true)
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.round_delete_forever_24),
//                        contentDescription = null
//                    )
//                }
//            }
        }

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
                            viewModel.deleteTasks()
                            showDeleteDialog = false
                            completedTasks = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }

            )
        }

        if (showTaskAddDialog) {
            var newTask by remember { mutableStateOf("") }
            var newPriority by remember { mutableStateOf(false) }
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showTaskAddDialog = false
                                newPriority = true
                                viewModel.addTask(
                                    Task(
                                        Date.from(Instant.now()).toString(),
                                        newTask,
                                        newPriority
                                    )
                                )
                                newTask = ""
                            },
                            enabled = newTask.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp, 4.dp, 4.dp, 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.add_urgent_task),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(2.dp))

                        Button(
                            onClick = {
                                showTaskAddDialog = false
                                viewModel.addTask(
                                    Task(
                                        Date.from(Instant.now()).toString(),
                                        newTask,
                                        newPriority
                                    )
                                )
                                newTask = ""
                            },
                            enabled = newTask.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.add_task),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}