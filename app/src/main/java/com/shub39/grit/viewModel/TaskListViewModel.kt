package com.shub39.grit.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.database.task.Task
import com.shub39.grit.database.task.TaskDatabase
import com.shub39.grit.notification.NotificationAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(applicationContext: Context) : ViewModel() {

    private val taskDatabase = TaskDatabase.getDatabase(applicationContext)
    private val tasksDao = taskDatabase.taskDao()
    private val _tasks = MutableStateFlow(listOf<Task>())
    private val scheduler = NotificationAlarmScheduler(applicationContext)

    val tasks: StateFlow<List<Task>> get() = _tasks

    init {
        viewModelScope.launch {
            _tasks.value = tasksDao.getTasks()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            _tasks.value += task
            tasksDao.addTask(task)
            _tasks.value = tasksDao.getTasks()
        }
    }

    fun updateTaskStatus(updatedTask: Task) {
        viewModelScope.launch {
            tasksDao.updateTask(updatedTask)
            _tasks.value = tasksDao.getTasks()
        }
    }

    fun deleteTasks(all: Boolean = false) {
        viewModelScope.launch {
            for (task in _tasks.value) {
                if (all) {
                    tasksDao.deleteTask(task)
                } else if (task.status) {
                    tasksDao.deleteTask(task)
                }
            }
            _tasks.value = tasksDao.getTasks()
        }
    }

    fun scheduleDeletion(preference: String) {
        scheduler.schedule(preference)
    }

    fun cancelScheduleDeletion(preference: String) {
        scheduler.cancel(preference)
    }
}