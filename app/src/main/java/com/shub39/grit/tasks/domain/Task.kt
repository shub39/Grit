package com.shub39.grit.tasks.domain

import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val categoryId: Long,
    var title: String,
    var index: Int = 0,
    var status: Boolean = false,
    var deadline: LocalDateTime? = null,
    var priority: TaskPriority = TaskPriority.MEDIUM
)
