package com.shub39.grit.tasks.data.repository

import com.shub39.grit.core.data.mappers.toTask
import com.shub39.grit.core.data.mappers.toTaskEntity
import com.shub39.grit.tasks.data.database.TasksDao
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.domain.TaskRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasksRepository(
    private val tasksDao: TasksDao
): TaskRepo {
    override fun getTasks(): Flow<List<Task>> {
        return tasksDao.getTasksFlow().map { entities ->
            entities.map { it.toTask() }
        }
    }

    override suspend fun upsertTask(task: Task) {
        tasksDao.upsertTask(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        tasksDao.deleteTask(task.toTaskEntity())
    }
}