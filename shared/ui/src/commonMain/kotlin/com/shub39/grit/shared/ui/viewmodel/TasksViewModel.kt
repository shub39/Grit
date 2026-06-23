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
package com.shub39.grit.shared.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.CategoryColors
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.shared.ui.task.TaskAction
import com.shub39.grit.shared.ui.task.TaskState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class TasksViewModel(
    @Provided private val repo: TaskRepo,
    @Provided private val scheduler: AlarmScheduler,
    @Provided private val datastore: SettingsDatastore,
) : ViewModel() {

    companion object {
        private const val REORDER_DELAY = 200L
    }

    private var savedJob: Job? = null
    private var observerJob: Job? = null

    private val _state = MutableStateFlow(TaskState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                observeTasks()
                observeDatastore()

                rescheduleAllTasks()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    fun onAction(action: TaskAction) {
        viewModelScope.launch {
            when (action) {
                is UpsertTask -> {
                    if (action.task.status) {
                        repo.upsertTask(action.task.copy(reminder = null))
                    } else {
                        repo.upsertTask(action.task)

                        scheduler.schedule(action.task)
                    }
                }

                DeleteTasks -> {
                    deleteTasks()
                }

                is ChangeCategory -> {
                    _state.update { it.copy(currentCategory = action.category) }
                }

                is AddCategory -> {
                    upsertCategory(action.category)

                    _state.update { it.copy(currentCategory = it.tasks.keys.firstOrNull()) }
                }

                is ReorderTasks -> {
                    for (pair in action.mapping) {
                        repo.updateTaskIndexById(pair.second.id, pair.first)
                    }
                }

                is ReorderCategories -> {
                    for (category in action.mapping) {
                        upsertCategory(category.second.copy(index = category.first))
                    }

                    delay(REORDER_DELAY)

                    _state.update { it.copy(currentCategory = it.tasks.keys.firstOrNull()) }
                }

                is DeleteCategory -> {
                    deleteCategory(action.category)

                    delay(REORDER_DELAY)

                    _state.update { it.copy(currentCategory = it.tasks.keys.firstOrNull()) }
                }

                is DeleteTask -> repo.deleteTask(action.task)

                is ToggleAddTaskSheet -> _state.update { it.copy(showAddTaskSheet = action.show) }
            }
        }
    }

    private fun observeDatastore() {
        observerJob?.cancel()
        observerJob =
            viewModelScope.launch {
                combine(datastore.getIs24Hr(), datastore.getTaskReorderPref()) {
                        is24Hr,
                        reorderTasks ->
                        _state.update { it.copy(is24Hour = is24Hr, reorderTasks = reorderTasks) }
                    }
                    .launchIn(this)
            }
    }

    private fun observeTasks() {
        savedJob?.cancel()
        savedJob =
            viewModelScope.launch {
                combine(repo.getTasksFlow(), repo.getCompletedTasksFlow()) { tasks, completedTasks
                        ->
                        _state.update { it.copy(tasks = tasks, completedTasks = completedTasks) }

                        if (_state.value.currentCategory == null) {
                            _state.update { it.copy(currentCategory = tasks.keys.firstOrNull()) }
                        }

                        if (tasks.isEmpty()) addDefault()
                    }
                    .launchIn(this)
            }
    }

    private suspend fun rescheduleAllTasks() {
        repo.getTasks().forEach { task -> scheduler.schedule(task) }
    }

    private suspend fun addDefault() {
        upsertCategory(Category(name = "Misc", color = CategoryColors.GRAY.color))
    }

    private suspend fun deleteTasks() {
        for (task in _state.value.completedTasks) {
            repo.deleteTask(task)
        }
    }

    private suspend fun upsertCategory(category: Category) {
        repo.upsertCategory(category)
    }

    private suspend fun deleteCategory(category: Category) {
        if (_state.value.currentCategory == category) {
            _state.update { it.copy(currentCategory = it.tasks.keys.first()) }
        }

        repo.deleteCategory(category)
    }
}
