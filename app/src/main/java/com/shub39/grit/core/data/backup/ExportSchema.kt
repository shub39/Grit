package com.shub39.grit.core.data.backup

import com.shub39.grit.tasks.data.database.TaskDatabase
import kotlinx.serialization.Serializable

@Serializable
data class ExportSchema(
    val tasksSchemaVersion: Int = TaskDatabase.SCHEMA_VERSION,
    val habitsSchemaVersion: Int = TaskDatabase.SCHEMA_VERSION,
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
    val days: String,
    val reminder: Boolean
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
    val title: String,
    val status: Boolean = false,
    val index: Int = 0,
    val reminder: Long? = null
)

@Serializable
data class CategorySchema(
    val id: Long = 0,
    val name: String,
    val index: Int = 0,
    val color: String
)