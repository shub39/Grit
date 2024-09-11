package com.shub39.grit.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.task.Task
import com.shub39.grit.viewModel.TaskListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    viewModel: TaskListViewModel,
    onStatusChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    paddingValues: PaddingValues
) {
    val tasks by viewModel.tasks.collectAsState()
    var sortedTasks = tasks.sortedBy { !it.priority }

    LaunchedEffect(tasks) {
        sortedTasks = tasks.sortedBy { !it.priority }
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .animateContentSize(),
    ) {
        items(sortedTasks, key = { it.id }) {
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