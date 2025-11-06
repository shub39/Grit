package com.shub39.grit.habits.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.time.DayOfWeek
import java.time.LocalDateTime

@Stable
@Immutable
data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val days: Set<DayOfWeek>,
    val index: Int,
    val reminder: Boolean
)
