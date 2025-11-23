package com.shub39.grit.server

import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.core.utils.RpcService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class RpcServiceImpl(
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val datastore: GritDatastore,
    private val billingHandler: BillingHandler
): RpcService {
    override fun getTaskData(): Flow<Map<Category, List<Task>>> = taskRepo.getTasksFlow()
    override fun getCompletedTasks(): Flow<List<Task>> = taskRepo.getCompletedTasksFlow()
    override fun getHabitData(): Flow<List<HabitWithAnalytics>> = habitRepo.getHabitStatus()
    override fun getCompletedHabits(): Flow<List<Long>> = habitRepo.getCompletedHabitIds()
    override fun overallAnalytics(): Flow<OverallAnalytics> = habitRepo.getOverallAnalytics()
    override fun startingDay(): Flow<DayOfWeek> = datastore.getStartOfTheWeekPref()
    override fun is24Hr(): Flow<Boolean> = datastore.getIs24Hr()
    override fun isUserSubscribed(): Flow<Boolean> = flow { emit(billingHandler.isPlusUser()) }

    override suspend fun updateTaskIndexById(id: Long, index: Int) = taskRepo.updateTaskIndexById(id, index)
    override suspend fun upsertTask(task: Task) = taskRepo.upsertTask(task)
    override suspend fun deleteTask(task: Task) = taskRepo.deleteTask(task)
    override suspend fun upsertCategory(category: Category) = taskRepo.upsertCategory(category)
    override suspend fun deleteCategory(category: Category) = taskRepo.deleteCategory(category)
    override suspend fun upsertHabit(habit: Habit) = habitRepo.upsertHabit(habit)
    override suspend fun deleteHabit(habitId: Long) = habitRepo.deleteHabit(habitId)
    override suspend fun insertHabitStatus(habitStatus: HabitStatus) = habitRepo.insertHabitStatus(habitStatus)
    override suspend fun deleteHabitStatus(id: Long, date: LocalDate) = habitRepo.deleteHabitStatus(id, date)
}