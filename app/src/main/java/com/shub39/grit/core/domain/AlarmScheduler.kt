package com.shub39.grit.core.domain

import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.habits.domain.Habit

interface AlarmScheduler {
    fun schedule(habit: Habit)
    fun schedule(task: Task)

    fun cancel(habit: Habit)
    fun cancel(task: Task)

    fun cancelAll()
}