package com.shub39.grit.server

import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.core.utils.RpcService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RpcServiceImpl(
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val datastore: GritDatastore,
    private val billingHandler: BillingHandler
): RpcService {
    override fun getTaskData(): Flow<Map<Category, List<Task>>> = taskRepo.getTasksFlow()
    override fun getCompletedTasks(): Flow<List<Task>> = taskRepo.getTasksFlow()
        .map { tasks ->
            tasks.values.flatten().filter { it.status }
        }
    override fun getHabitData(): Flow<List<HabitWithAnalytics>> = habitRepo.getHabitStatus()
    @OptIn(ExperimentalTime::class)
    override fun getCompletedHabits(): Flow<List<Long>> = habitRepo.getHabitStatus()
        .map { habitWithAnalytics ->
            habitWithAnalytics.filter { data ->
                data.statuses.any {
                    it.date == Clock.System.todayIn(TimeZone.currentSystemDefault())
                }
            }.map { it.habit.id }
        }
    override fun overallAnalytics(): Flow<OverallAnalytics> = habitRepo.getOverallAnalytics()
    override fun startingDay(): Flow<DayOfWeek> = datastore.getStartOfTheWeekPref()
    override fun is24Hr(): Flow<Boolean> = datastore.getIs24Hr()
    override fun isUserSubscribed(): Flow<Boolean> {
        return flow {
            emit(billingHandler.isPlusUser())
        }
    }
}