package com.shub39.grit.database.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val priority: Boolean,
    var status: Boolean = false
)