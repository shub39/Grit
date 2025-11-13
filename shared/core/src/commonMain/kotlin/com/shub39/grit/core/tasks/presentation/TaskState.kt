package com.shub39.grit.core.tasks.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task

@Stable
@Immutable
data class TaskState(
    val tasks: Map<Category, List<Task>> = emptyMap(),
    val currentCategory: Category? = null,
    val completedTasks: List<Task> = emptyList(),
    val is24Hour: Boolean = false,
    val reorderTasks: Boolean = true
)