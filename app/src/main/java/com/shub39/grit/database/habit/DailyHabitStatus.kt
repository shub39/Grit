package com.shub39.grit.database.habit

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity
data class DailyHabitStatus(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: Long,
    val completed: Boolean
)