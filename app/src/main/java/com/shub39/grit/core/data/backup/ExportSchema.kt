package com.shub39.grit.core.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class ExportSchema(
    val schemaVersion: Int = 4,
    val habits: List<HabitSchema>,
    val habitStatus: List<HabitStatusSchema>,
    val tasks: List<TaskSchema>,
    val categories: List<CategorySchema>
)

@Serializable
data class HabitSchema(
    val id: Long = 0,
    val title: String,
    val description: String,
    val index: Int,
    val time: Long,
    val days: String
)

@Serializable
data class HabitStatusSchema(
    val id: Long = 0,
    val habitId: Long,
    val date: Long,
)

@Serializable
data class TaskSchema(
    val id: Long = 0,
    val categoryId: Long,
    var title: String,
    var status: Boolean = false,
    var index: Int = 0
)

@Serializable
data class CategorySchema(
    val id: Long = 0,
    val name: String,
    val index: Int = 0,
    val color: String
)