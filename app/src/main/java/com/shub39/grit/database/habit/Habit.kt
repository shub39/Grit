package com.shub39.grit.database.habit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "habit_index")
data class Habit(
    @PrimaryKey val id: String,
    val description: String,
    val time: Long
)