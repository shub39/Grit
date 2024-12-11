package com.shub39.grit.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.data.GritDatastore
import com.shub39.grit.core.domain.NotificationAlarmScheduler
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.domain.TaskRepo
import com.shub39.grit.tasks.presentation.task_page.TaskPageAction
import com.shub39.grit.tasks.presentation.task_page.TaskPageState
import com.shub39.grit.tasks.presentation.tasks_settings.TasksSettingsAction
import com.shub39.grit.tasks.presentation.tasks_settings.TasksSettingsState
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
    private val repo: TaskRepo,
    private val scheduler: NotificationAlarmScheduler,
    private val datastore: GritDatastore
) : ViewModel() {

    private var savedJob: Job? = null
    private var settingsJob: Job? = null
    private val _tasksState = MutableStateFlow(TaskPageState())
    private val _tasksSettings = MutableStateFlow(TasksSettingsState())

    val tasksState = _tasksState.asStateFlow()
        .onStart {
            // runs when flow starts
            observeTasks()
            observeSettings()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TaskPageState()
        )

    val tasksSettings = _tasksSettings.asStateFlow()
        .onStart {
            observeSettings()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TasksSettingsState()
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

                is TaskPageAction.changeCategory -> {
                    _tasksState.update {
                        it.copy(
                            currentCategory = action.category
                        )
                    }
                }
            }
        }
    }

    fun tasksSettingsAction(action: TasksSettingsAction) {
        viewModelScope.launch {
            when (action) {
                is TasksSettingsAction.UpdateClearPreference -> {
                    cancelScheduleDeletion(_tasksSettings.value.currentClearPreference)
                    datastore.setClearPreferences(action.clearPreference)
                    scheduleDeletion(action.clearPreference)
                }
            }
        }
    }

    private fun observeSettings() {
        settingsJob?.cancel()
        settingsJob = datastore
            .clearPreferences()
            .onEach { preference ->
                _tasksSettings.update { settings ->
                    settings.copy(
                        currentClearPreference = preference
                    )
                }
            }
            .launchIn(viewModelScope)
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
                        currentCategory = tasks.keys.first()
                    )
                }

                if (tasks.isEmpty()) {
                    addDefault()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun addDefault() {
        repo.upsertCategory(Category(name = "Misc", color = CategoryColors.GRAY.color))
    }

    private suspend fun upsertTask(task: Task) {
        repo.upsertTask(task)
    }

    private suspend fun deleteTasks() {
        for (task in _tasksState.value.completedTasks) {
            repo.deleteTask(task)
        }
    }

    private suspend fun addCategory(category: Category) {
        repo.upsertCategory(category)
    }

    private suspend fun deleteCategory(category: Category) {
        repo.deleteCategory(category)
    }

    // schedule deletion to user preference, needs testing
    private fun scheduleDeletion(preference: String) {
        scheduler.schedule(preference)
    }

    private fun cancelScheduleDeletion(preference: String) {
        scheduler.cancel(preference)
    }
}