package com.shub39.grit.ui.page.task_page

import androidx.compose.runtime.Immutable
import com.shub39.grit.database.task.Task

// needs parameters for sorted tasks and other things
@Immutable
data class TaskPageState(
    val tasks: List<Task> = emptyList(),
    val completedTasks: Int = 0,

)
