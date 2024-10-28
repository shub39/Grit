package com.shub39.grit.ui.page.task_page

import androidx.compose.runtime.Immutable
import com.shub39.grit.database.task.Task

@Immutable
data class TaskPageState(
    val tasks: List<Task>,
)
