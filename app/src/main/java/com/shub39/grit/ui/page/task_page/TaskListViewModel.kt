package com.shub39.grit.ui.page.task_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.task.Task
import com.shub39.grit.database.task.TaskDatabase
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    taskDatabase: TaskDatabase,
    scheduler: NotificationAlarmScheduler
) : ViewModel() {

    private val tasksDao = taskDatabase.taskDao()

    private val _tasksState = MutableStateFlow(TaskPageState())
    private val _scheduler = scheduler

    val tasksState = _tasksState.asStateFlow()
        .onStart {
            // runs when flow starts
            getTasks()
            updateCompleted()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TaskPageState()
        )

    // handles actions from task page
    fun taskPageAction(action: TaskPageAction) {
        viewModelScope.launch {
            when (action) {
                is TaskPageAction.AddTask -> {
                    addTask(action.task)
                    getTasks()
                }

                TaskPageAction.DeleteTasks -> {
                    deleteTasks()
                    getTasks()
                    updateCompleted()
                }

                is TaskPageAction.UpdateTaskStatus -> {
                    updateTaskStatus(action.task)
                    getTasks()
                    updateCompleted()
                }
            }
        }
    }

    private suspend fun getTasks() {
        _tasksState.update {
            it.copy(
                tasks = tasksDao.getTasks()
            )
        }
    }

    private suspend fun addTask(task: Task) {
        tasksDao.addTask(task)
    }

    private suspend fun updateTaskStatus(updatedTask: Task) {
        tasksDao.updateTask(updatedTask)
    }

    private fun updateCompleted() {
        var completed = 0

        for (task in _tasksState.value.tasks) {
            if (task.status) {
                completed++
            }
        }

        _tasksState.update {
            it.copy(
                completedTasks = completed
            )
        }
    }

    private suspend fun deleteTasks(all: Boolean = false) {
        for (task in _tasksState.value.tasks) {
            if (all) {
                tasksDao.deleteTask(task)
            } else if (task.status) {
                tasksDao.deleteTask(task)
            }
        }
    }

    // schedule deletion to user preference, needs testing
    fun scheduleDeletion(preference: String) {
        _scheduler.schedule(preference)
    }

    fun cancelScheduleDeletion(preference: String) {
        _scheduler.cancel(preference)
    }
}