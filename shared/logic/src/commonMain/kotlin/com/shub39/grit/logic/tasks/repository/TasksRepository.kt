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
package com.shub39.grit.logic.tasks.repository

import com.shub39.grit.core.interfaces.AppNotificationManager
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.core.tasks.TaskWithSubTasks
import com.shub39.grit.logic.tasks.database.CategoryDao
import com.shub39.grit.logic.tasks.database.SubTaskDao
import com.shub39.grit.logic.tasks.database.TasksDao
import com.shub39.grit.logic.tasks.toCategory
import com.shub39.grit.logic.tasks.toCategoryEntity
import com.shub39.grit.logic.tasks.toSubTask
import com.shub39.grit.logic.tasks.toSubTaskEntity
import com.shub39.grit.logic.tasks.toTask
import com.shub39.grit.logic.tasks.toTaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [TaskRepo::class])
class TasksRepository(
    private val tasksDao: TasksDao,
    private val categoryDao: CategoryDao,
    private val subTaskDao: SubTaskDao,
    private val notificationManager: AppNotificationManager,
) : TaskRepo {

    private val tasksFlow =
        tasksDao
            .getTasksFlow()
            .map { entities -> entities.map { it.toTask() }.sortedBy { it.index } }
            .flowOn(Dispatchers.IO)

    val categoriesFlow =
        categoryDao
            .getCategoriesFlow()
            .map { entities -> entities.map { it.toCategory() }.sortedBy { it.index } }
            .flowOn(Dispatchers.IO)

    val subTasksFlow =
        subTaskDao
            .getSubTasksFlow()
            .map { entities -> entities.map { it.toSubTask() }.sortedBy { it.index } }
            .flowOn(Dispatchers.IO)

    override fun getTasksFlow(): Flow<Map<Category, List<TaskWithSubTasks>>> {
        return combine(tasksFlow, categoriesFlow, subTasksFlow) { tasks, categories, subTasks ->
            categories.associateWith {
                tasks
                    .filter { task -> task.categoryId == it.id }
                    .map { task ->
                        TaskWithSubTasks(
                            task,
                            subTasks.filter { subTask -> subTask.taskId == task.id },
                        )
                    }
            }
        }
    }

    override fun getCompletedTasksFlow(): Flow<List<Task>> {
        return tasksFlow.map { tasks -> tasks.filter { it.status } }.flowOn(Dispatchers.IO)
    }

    override suspend fun getTasks(): List<Task> {
        return tasksDao.getTasks().map { it.toTask() }
    }

    override suspend fun getSubTasks(): List<SubTask> {
        return subTaskDao.getSubTasks().map { it.toSubTask() }
    }

    override suspend fun upsertSubTask(subTask: SubTask) {
        subTaskDao.upsertSubTask(subTask.toSubTaskEntity())
    }

    override suspend fun deleteSubTask(subTask: SubTask) {
        subTaskDao.deleteSubTask(subTask.toSubTaskEntity())
    }

    override suspend fun getTaskById(id: Long): Task? {
        return tasksDao.getTaskById(id)?.toTask()
    }

    override suspend fun getCategories(): List<Category> {
        return categoryDao.getCategories().map { it.toCategory() }
    }

    override suspend fun updateTaskIndexById(id: Long, index: Int) {
        tasksDao.updateTaskIndexById(id, index)
    }

    override suspend fun upsertTask(task: Task): Long {
        return if (task.id == 0L) {
            tasksDao.upsertTask(task.toTaskEntity())
        } else {
            tasksDao.upsertTask(task.toTaskEntity())
            if (task.status) {
                notificationManager.cancelNotification(task)
            }

            task.id
        }
    }

    override suspend fun deleteTask(task: Task) {
        tasksDao.deleteTask(task.toTaskEntity())
    }

    override suspend fun deleteAllTasks() {
        tasksDao.deleteAllTasks()
    }

    override suspend fun upsertCategory(category: Category) {
        categoryDao.upsertCategory(category.toCategoryEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toCategoryEntity())
    }

    override suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }
}
