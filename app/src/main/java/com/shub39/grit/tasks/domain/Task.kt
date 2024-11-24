package com.shub39.grit.tasks.domain

data class Task(
    val id: String,
    var title: String,
    val priority: Boolean,
    var status: Boolean = false
)
