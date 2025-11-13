package com.shub39.grit.core.domain

import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.tasks.domain.Task

interface AlarmScheduler {
    fun schedule(habit: Habit)
    fun schedule(task: Task)

    fun cancel(habit: Habit)
    fun cancel(task: Task)

    fun cancelAll()
}