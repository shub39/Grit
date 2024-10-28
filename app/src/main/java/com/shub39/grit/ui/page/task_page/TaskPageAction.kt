package com.shub39.grit.ui.page.task_page

import com.shub39.grit.database.task.Task

sealed interface TaskPageAction {
    data class UpdateTaskStatus(val task: Task): TaskPageAction
    object DeleteTasks: TaskPageAction
    data class AddTask(val task: Task): TaskPageAction
}