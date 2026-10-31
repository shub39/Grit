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
package com.shub39.grit.core.tasks

import com.shub39.grit.core.interfaces.AlarmScheduler

/**
 * Single write path for task and subtask status, shared by the view models, the notification
 * receiver and the home screen widgets so that they all produce the same state.
 */
class TasksStatusHandler(private val repo: TaskRepo, private val scheduler: AlarmScheduler) {

    suspend fun toggleTask(task: Task) {
        if (task.status) reopenTask(task) else completeTask(task)
    }

    /**
     * Completes [task] and all of its subtasks, clearing their reminders and cancelling their
     * alarms.
     */
    suspend fun completeTask(task: Task) {
        repo.upsertTask(task.copy(status = true, reminder = null))
        scheduler.cancel(task)

        repo
            .getSubTasks()
            .filter { it.taskId == task.id && !it.status }
            .forEach { subTask -> completeSubTask(subTask) }
    }

    suspend fun reopenTask(task: Task) {
        repo.upsertTask(task.copy(status = false))
        scheduler.schedule(task.copy(status = false))
    }

    suspend fun toggleSubTask(subTask: SubTask) {
        if (subTask.status) {
            repo.upsertSubTask(subTask.copy(status = false))
            scheduler.schedule(subTask.copy(status = false))
        } else {
            completeSubTask(subTask)
        }
    }

    suspend fun completeSubTask(subTask: SubTask) {
        repo.upsertSubTask(subTask.copy(status = true, reminder = null))
        scheduler.cancel(subTask)
    }
}
