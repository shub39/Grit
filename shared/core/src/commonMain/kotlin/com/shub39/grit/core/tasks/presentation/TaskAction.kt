/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.tasks.presentation

import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task

sealed interface TaskAction {
    data class AddCategory(val category: Category) : TaskAction

    data class ChangeCategory(val category: Category) : TaskAction

    data class DeleteCategory(val category: Category) : TaskAction

    data object DeleteTasks : TaskAction

    data class DeleteTask(val task: Task) : TaskAction

    data class ReorderTasks(val mapping: List<Pair<Int, Task>>) : TaskAction

    data class ReorderCategories(val mapping: List<Pair<Int, Category>>) : TaskAction

    data class UpsertTask(val task: Task) : TaskAction
}
