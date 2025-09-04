package com.shub39.grit.core.domain

// all the different intent actions passed
enum class IntentActions(val action: String) {
    ADD_HABIT_STATUS("add_habit_status"),
    HABIT_NOTIFICATION("habit"),
    TASK_NOTIFICATION("task_notification"),
    MARK_TASK_DONE("mark_task_done")
}