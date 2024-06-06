package com.shub39.grit.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.components.Done
import com.shub39.grit.viewModel.TaskListViewModel
import com.shub39.grit.components.TaskCard
import com.shub39.grit.database.task.Task
import java.time.Instant
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoPage(viewModel: TaskListViewModel) {
    var showTaskAddDialog by remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()
    val taskListIsEmpty = tasks.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.tasks_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTaskAddDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_add_24),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = stringResource(id = R.string.add_task))
                }
            }
        }
    ) { innerPadding ->
        if (showTaskAddDialog) {
            var newTask by remember {
                mutableStateOf("")
            }
            var newPriority by remember {
                mutableStateOf(false)
            }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember {
                FocusRequester()
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            AlertDialog(
                onDismissRequest = { showTaskAddDialog = false },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTask,
                            onValueChange = { newTask = it },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.focusRequester(focusRequester)
                        )
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = newPriority,
                                onCheckedChange = { newPriority = it },
                                enabled = newTask.isNotEmpty()
                            )
                            Text(text = stringResource(id = R.string.urgent))
                        }
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
                            enabled = newTask.isNotEmpty()
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

        if (taskListIsEmpty) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.empty),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            TaskList(
                viewModel = viewModel,
                onStatusChange = { updatedTask ->
                    viewModel.updateTaskStatus(updatedTask)
                },
                onDeleteTask = { deletedTask ->
                    viewModel.deleteTask(deletedTask)
                },
                paddingValues = innerPadding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    viewModel: TaskListViewModel,
    onStatusChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    paddingValues: PaddingValues
) {
    val tasks by viewModel.tasks.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(tasks, key = { it.id }) {
            val dismissState = rememberSwipeToDismissBoxState(
                initialValue = SwipeToDismissBoxValue.Settled,
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                        onDeleteTask(it)
                        true
                    } else {
                        false
                    }
                },
                positionalThreshold = { threshold ->
                    threshold * 0.8F
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromEndToStart = true,
                backgroundContent = { Done() }
            ) {
                TaskCard(
                    task = it,
                    onStatusChange = onStatusChange,
                )
            }
        }
    }
}