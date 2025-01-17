package com.shub39.grit.tasks.presentation.task_page

import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task

sealed interface TaskPageAction {
    data class UpdateTaskStatus(val task: Task): TaskPageAction
    data class AddCategory(val category: Category): TaskPageAction
    data class ChangeCategory(val category: Category): TaskPageAction
    data class DeleteCategory(val category: Category): TaskPageAction
    data object DeleteTasks: TaskPageAction
    data class ReorderTasks(val mapping: List<Pair<Int, Task>>): TaskPageAction
    data class ReorderCategories(val mapping: List<Pair<Int, Category>>): TaskPageAction
    data class AddTask(val task: Task): TaskPageAction
}