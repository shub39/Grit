package com.shub39.grit.tasks.presentation

import com.shub39.grit.tasks.data.database.Task

sealed interface TaskPageAction {
    data class UpdateTaskStatus(val task: Task): TaskPageAction
    object DeleteTasks: TaskPageAction
    data class AddTask(val task: Task): TaskPageAction
}