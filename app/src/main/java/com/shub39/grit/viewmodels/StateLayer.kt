package com.shub39.grit.viewmodels

import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.tasks.presentation.TaskState
import kotlinx.coroutines.flow.MutableStateFlow

class StateLayer {
    val tasksState = MutableStateFlow(TaskState())
    val habitsState = MutableStateFlow(HabitState())
    val settingsState = MutableStateFlow(SettingsState())
}