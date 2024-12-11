package com.shub39.grit.tasks.presentation.task_page

import androidx.compose.runtime.Immutable
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task

// needs parameters for sorted tasks and other things
@Immutable
data class TaskPageState(
    val tasks: Map<Category, List<Task>> = emptyMap(),
    val currentCategory: Category? = null,
    val completedTasks: List<Task> = emptyList()
)
