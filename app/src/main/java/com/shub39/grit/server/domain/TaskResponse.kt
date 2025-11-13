package com.shub39.grit.server.domain

import com.shub39.grit.core.tasks.domain.Task
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Long,
    val categoryId: Long,
    var title: String,
    val index: Int = 0,
    val status: Boolean = false,
)

fun Task.toTaskResponse(): TaskResponse {
    return TaskResponse(
        id = id,
        title = title,
        categoryId = categoryId,
        index = index,
        status = status,
    )
}

fun TaskResponse.toTask(): Task {
    return Task(
        id = id,
        categoryId = categoryId,
        title = title,
        index = index,
        status = status,
        reminder = null
    )
}