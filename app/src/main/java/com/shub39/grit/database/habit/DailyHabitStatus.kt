package com.shub39.grit.database.habit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "daily_habit_status")
data class DailyHabitStatus(
    @PrimaryKey val key: LocalDateTime,
    val id: String,
    val date: LocalDate,
)