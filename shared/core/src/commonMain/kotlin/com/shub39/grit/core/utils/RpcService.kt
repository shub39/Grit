package com.shub39.grit.core.utils

import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.rpc.annotations.Rpc

@Rpc
interface RpcService {
    fun getTaskData(): Flow<Map<Category, List<Task>>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getHabitData(): Flow<List<HabitWithAnalytics>>
    fun getCompletedHabits(): Flow<List<Long>>
    fun overallAnalytics(): Flow<OverallAnalytics>
    fun startingDay(): Flow<DayOfWeek>
    fun is24Hr(): Flow<Boolean>
    fun isUserSubscribed(): Flow<Boolean>

    suspend fun updateTaskIndexById(id: Long, index: Int)
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    suspend fun upsertHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Long)
    suspend fun insertHabitStatus(habitStatus: HabitStatus)
    suspend fun deleteHabitStatus(id: Long, date: LocalDate)
}