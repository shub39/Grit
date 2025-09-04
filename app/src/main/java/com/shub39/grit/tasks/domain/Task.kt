package com.shub39.grit.tasks.domain

import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val categoryId: Long,
    var title: String,
    val index: Int = 0,
    val status: Boolean = false,
    val reminder: LocalDateTime? = null
)
