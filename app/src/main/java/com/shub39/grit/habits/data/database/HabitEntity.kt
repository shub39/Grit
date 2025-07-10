package com.shub39.grit.habits.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDateTime

@Entity(tableName = "habit_index")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val index: Int,
    val days: Set<DayOfWeek>,
    val time: LocalDateTime
)