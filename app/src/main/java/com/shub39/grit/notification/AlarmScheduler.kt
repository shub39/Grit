package com.shub39.grit.notification

import com.shub39.grit.database.habit.Habit

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