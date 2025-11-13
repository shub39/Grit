package com.shub39.grit.viewmodels

import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.habits.presentation.HabitPageState
import kotlinx.coroutines.flow.MutableStateFlow

class StateLayer {
    val tasksState = MutableStateFlow(TaskState())
    val habitsState = MutableStateFlow(HabitPageState())
    val settingsState = MutableStateFlow(SettingsState())
}