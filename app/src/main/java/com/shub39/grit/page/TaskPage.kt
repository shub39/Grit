package com.shub39.grit.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.Done
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.viewModel.TaskListViewModel
import com.shub39.grit.component.TaskCard
import com.shub39.grit.database.task.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

@Composable
fun TodoPage(
    viewModel: TaskListViewModel,
) {
    var showTaskAddDialog by remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()
    val taskListIsEmpty = tasks.isEmpty()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTaskAddDialog = true },
                shape = MaterialTheme.shapes.extraLarge
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
                }
            }
        }
    ) { innerPadding ->
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
        if (taskListIsEmpty) {
            EmptyPage(paddingValues = innerPadding)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    viewModel: TaskListViewModel,
    onStatusChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    paddingValues: PaddingValues
) {
    val tasks by viewModel.tasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .animateContentSize()
    ) {
        items(tasks.sortedBy { !it.priority }, key = { it.id }) {
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
                backgroundContent = { Done(dismissState) },
                enableDismissFromEndToStart = false,
                modifier = Modifier.animateItemPlacement()
            ) {
                TaskCard(
                    task = it,
                    onStatusChange = onStatusChange,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}