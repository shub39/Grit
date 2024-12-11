package com.shub39.grit.tasks.domain

data class Task(
    val id: Long = 0,
    val categoryId: Long,
    var title: String,
    var status: Boolean = false
)
