package com.shub39.grit.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.shub39.grit.database.task.Task
import com.shub39.grit.database.task.TaskDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(applicationContext: Context) : ViewModel() {

    private val taskDatabase = Room.databaseBuilder(
        applicationContext,
        TaskDatabase::class.java,
        "task_database"
    ).build()
    private val tasksDao = taskDatabase.taskDao()
    private val _tasks = MutableStateFlow(listOf<Task>())

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
        }
    }

    fun updateTaskStatus(updatedTask: Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == updatedTask.id) updatedTask else task
            }
            tasksDao.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            _tasks.value = _tasks.value.filter { it.id != task.id }
            tasksDao.deleteTask(task)
        }
    }
}