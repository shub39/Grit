package com.shub39.grit.habits.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "habit_index")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val index: Int,
    val days: Set<DayOfWeek>,
    val time: LocalDateTime,
    @ColumnInfo(name = "reminder", defaultValue = "1")
    val reminder: Boolean
)