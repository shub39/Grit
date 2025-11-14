package com.shub39.grit.core.habits.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class HabitStatus(
    val id: Long = 0,
    val habitId: Long,
    val date: LocalDate
)