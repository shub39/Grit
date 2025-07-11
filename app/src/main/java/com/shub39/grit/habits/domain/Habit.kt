package com.shub39.grit.habits.domain

import java.time.DayOfWeek
import java.time.LocalDateTime

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val days: Set<DayOfWeek>,
    val index: Int
)
