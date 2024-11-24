package com.shub39.grit.habits.domain

import java.time.LocalDate

data class HabitStatus(
    val id: Long = 0,
    val habitId: Long,
    val date: LocalDate
)
