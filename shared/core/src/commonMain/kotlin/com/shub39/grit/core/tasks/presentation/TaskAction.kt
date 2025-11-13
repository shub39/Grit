package com.shub39.grit.core.tasks.presentation

import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task

sealed interface TaskAction {
    data class AddCategory(val category: Category): TaskAction
    data class ChangeCategory(val category: Category): TaskAction
    data class DeleteCategory(val category: Category): TaskAction
    data object DeleteTasks: TaskAction
    data class ReorderTasks(val mapping: List<Pair<Int, Task>>): TaskAction
    data class ReorderCategories(val mapping: List<Pair<Int, Category>>): TaskAction
    data class UpsertTask(val task: Task): TaskAction
}