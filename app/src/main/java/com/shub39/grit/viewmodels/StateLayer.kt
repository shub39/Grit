package com.shub39.grit.viewmodels

import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.tasks.presentation.task_page.TaskPageState
import kotlinx.coroutines.flow.MutableStateFlow

class StateLayer {
    val tasksState = MutableStateFlow(TaskPageState())
    val habitsState = MutableStateFlow(HabitPageState())
}