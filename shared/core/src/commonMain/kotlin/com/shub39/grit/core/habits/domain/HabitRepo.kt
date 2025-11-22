package com.shub39.grit.core.habits.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HabitRepo {
    suspend fun upsertHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Long)

    suspend fun getHabits(): List<Habit>
    suspend fun getHabitById(id: Long): Habit?
    suspend fun getHabitStatuses(): List<HabitStatus>

    fun getHabitStatus(): Flow<List<HabitWithAnalytics>>
    fun getCompletedHabitIds(): Flow<List<Long>>
    fun getOverallAnalytics(): Flow<OverallAnalytics>

    suspend fun getStatusForHabit(id: Long): List<HabitStatus>
    suspend fun insertHabitStatus(habitStatus: HabitStatus)
    suspend fun deleteHabitStatus(id: Long, date: LocalDate)
}