package com.shub39.grit.habits.domain

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepo {
    fun getHabits(): Flow<List<Habit>>
    suspend fun upsertHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Long)

    fun getHabitStatus(): Flow<Map<Long, List<HabitStatus>>>
    suspend fun insertHabitStatus(habitStatus: HabitStatus)
    suspend fun deleteHabitStatus(id: Long, date: LocalDate)
}