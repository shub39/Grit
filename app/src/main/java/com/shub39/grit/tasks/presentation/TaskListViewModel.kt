package com.shub39.grit.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.domain.TaskRepo
import com.shub39.grit.tasks.presentation.task_page.TaskPageAction
import com.shub39.grit.tasks.presentation.task_page.TaskPageState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repo: TaskRepo
) : ViewModel() {

    private var savedJob: Job? = null
    private val _tasksState = MutableStateFlow(TaskPageState())

    val tasksState = _tasksState.asStateFlow()
        .onStart {
            // runs when flow starts
            observeTasks()
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
                    upsertTask(action.task)
                }

                TaskPageAction.DeleteTasks -> {
                    deleteTasks()
                }

                is TaskPageAction.UpdateTaskStatus -> {
                    upsertTask(action.task)
                }

                is TaskPageAction.ChangeCategory -> {
                    _tasksState.update {
                        it.copy(
                            currentCategory = action.category
                        )
                    }
                }

                is TaskPageAction.AddCategory -> {
                    upsertCategory(action.category)

                    _tasksState.update {
                        it.copy(
                            currentCategory = it.tasks.keys.firstOrNull()
                        )
                    }
                }

                is TaskPageAction.ReorderTasks -> {
                    for (task in action.mapping) {
                        upsertTask(task.second.copy(index = task.first))
                    }
                }

                is TaskPageAction.ReorderCategories -> {
                    for (category in action.mapping) {
                        upsertCategory(category.second.copy(index = category.first))
                    }

                    _tasksState.update {
                        it.copy(
                            currentCategory = it.tasks.keys.firstOrNull()
                        )
                    }
                }

                is TaskPageAction.DeleteCategory -> {
                    deleteCategory(action.category)

                    _tasksState.update {
                        it.copy(
                            currentCategory = it.tasks.keys.firstOrNull()
                        )
                    }
                }
            }
        }
    }

    private fun observeTasks() {
        savedJob?.cancel()
        savedJob = repo
            .getTasks()
            .onEach { tasks ->
                _tasksState.update { task ->
                    task.copy(
                        tasks = tasks,
                        completedTasks = tasks.values.flatten().filter { it.status },
                    )
                }

                if (_tasksState.value.currentCategory == null) {
                    _tasksState.update {
                        it.copy(
                            currentCategory = tasks.keys.firstOrNull()
                        )
                    }
                }

                if (tasks.isEmpty()) {
                    addDefault()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun addDefault() {
        upsertCategory(Category(name = "Misc", color = CategoryColors.GRAY.color))
    }

    private suspend fun upsertTask(task: Task) {
        repo.upsertTask(task)
    }

    private suspend fun deleteTasks() {
        for (task in _tasksState.value.completedTasks) {
            repo.deleteTask(task)
        }
    }

    private suspend fun upsertCategory(category: Category) {
        repo.upsertCategory(category)
    }

    private suspend fun deleteCategory(category: Category) {
        if (_tasksState.value.currentCategory == category) {
            _tasksState.update {
                it.copy(
                    currentCategory = it.tasks.keys.first()
                )
            }
        }

        repo.deleteCategory(category)
    }
}