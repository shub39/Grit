package com.shub39.grit.core.habits.domain

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val days: Set<DayOfWeek>,
    val index: Int,
    val reminder: Boolean
)