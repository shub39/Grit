package com.shub39.grit.tasks.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task

// needs parameters for sorted tasks and other things
@Stable
@Immutable
data class TaskPageState(
    val tasks: Map<Category, List<Task>> = emptyMap(),
    val currentCategory: Category? = null,
    val completedTasks: List<Task> = emptyList(),
    val is24Hour: Boolean = false,
    val reorderTasks: Boolean = true
)