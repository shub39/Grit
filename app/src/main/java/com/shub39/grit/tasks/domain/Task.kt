package com.shub39.grit.tasks.domain

import kotlinx.datetime.LocalDateTime

data class Task(
    val id: Long = 0,
    val categoryId: Long,
    val title: String,
    val index: Int = 0,
    val status: Boolean = false,
    val reminder: LocalDateTime? = null
)
