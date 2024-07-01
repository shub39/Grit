package com.shub39.grit.notification

import com.shub39.grit.database.habit.Habit

interface AlarmScheduler {
    fun schedule(item: Habit)
    fun schedule(preference: String)
    fun cancel(item: Habit)
    fun cancel(preference: String)
}