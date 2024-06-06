package com.shub39.grit.database.habit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    var status: Boolean
)