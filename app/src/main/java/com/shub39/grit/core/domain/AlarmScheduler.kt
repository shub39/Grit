package com.shub39.grit.core.domain

import com.shub39.grit.habits.domain.Habit

interface AlarmScheduler {
    // Notification scheduler for Habits
    fun schedule(item: Habit)

    // deletion scheduler for tasks
    fun schedule(preference: String)

    // cancel habit notifications
    fun cancel(item: Habit)

    // cancel task deletion
    fun cancel(preference: String)
}