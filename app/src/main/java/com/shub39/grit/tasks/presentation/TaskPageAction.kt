package com.shub39.grit.tasks.presentation

import com.shub39.grit.tasks.domain.Task

sealed interface TaskPageAction {
    data class UpdateTaskStatus(val task: Task): TaskPageAction
    data object DeleteTasks: TaskPageAction
    data class AddTask(val task: Task): TaskPageAction
}