package com.shub39.grit.tasks.presentation

import androidx.compose.runtime.Immutable
import com.shub39.grit.tasks.data.database.Task

// needs parameters for sorted tasks and other things
@Immutable
data class TaskPageState(
    val tasks: List<Task> = emptyList(),
    val completedTasks: Int = 0,

    )
