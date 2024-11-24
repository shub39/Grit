package com.shub39.grit.tasks.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey val id: String,
    var title: String,
    val priority: Boolean,
    var status: Boolean = false
)