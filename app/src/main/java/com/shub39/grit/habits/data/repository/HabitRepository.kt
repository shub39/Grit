package com.shub39.grit.habits.data.repository

import com.shub39.grit.core.data.GritNotificationManager
import com.shub39.grit.core.data.toHabit
import com.shub39.grit.core.data.toHabitEntity
import com.shub39.grit.core.data.toHabitStatus
import com.shub39.grit.core.data.toHabitStatusEntity
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import com.shub39.grit.core.utils.now
import com.shub39.grit.habits.data.database.HabitStatusDao
import com.shub39.grit.habits.data.database.HabitsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime

@Single(binds = [HabitRepo::class])
@OptIn(ExperimentalTime::class)
class HabitRepository(
    private val habitDao: HabitsDao,
    private val habitStatusDao: HabitStatusDao,
    private val datastore: GritDatastore,
    private val notificationManager: GritNotificationManager
) : HabitRepo {

    private val habits = habitDao
        .getAllHabitsFlow()
        .map { habits ->
            habits.map { it.toHabit() }.sortedBy { it.index }
        }
        .flowOn(Dispatchers.IO)

    private val habitStatuses = habitStatusDao
        .getAllHabitStatuses()
        .map { habitStatuses ->
            habitStatuses.map { it.toHabitStatus() }
        }
        .flowOn(Dispatchers.IO)

    private val firstDayOfWeek = MutableStateFlow(DayOfWeek.MONDAY)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            datastore.getStartOfTheWeekPref()
                .onEach { firstDayOfWeek.update { it } }
                .launchIn(this)
        }
    }

    override suspend fun upsertHabit(habit: Habit) {
        habitDao.upsertHabit(habit.toHabitEntity())
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
    }

    override suspend fun getHabits(): List<Habit> {
        return habitDao.getAllHabits().map { it.toHabit() }
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)?.toHabit()
    }

    override suspend fun getHabitStatuses(): List<HabitStatus> {
        return habitStatusDao.getHabitStatuses().map { it.toHabitStatus() }
    }

    override fun getHabitsWithAnalytics(): Flow<List<HabitWithAnalytics>> {
        return habits.combine(habitStatuses) { habitsFlow, habitStatusesFlow ->
            habitsFlow.map { habit ->
                val habitStatusesForHabit = habitStatusesFlow.filter { it.habitId == habit.id }
                val dates = habitStatusesForHabit.map { it.date }

                HabitWithAnalytics(
                    habit = habit,
                    statuses = habitStatusesForHabit,
                    currentStreak = countCurrentStreak(
                        dates = dates,
                        eligibleWeekdays = habit.days
                    ),
                    bestStreak = countBestStreak(dates = dates, eligibleWeekdays = habit.days),
                    weeklyComparisonData = prepareLineChartData(
                        firstDay = firstDayOfWeek.value,
                        habitStatuses = habitStatusesForHabit
                    ),
                    weekDayFrequencyData = prepareWeekDayFrequencyData(dates = dates),
                    startedDaysAgo = habit.time.date.daysUntil(LocalDate.now()).toLong()
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override fun getCompletedHabitIds(): Flow<List<Long>> {
        return habitStatuses
            .map { habitStatuses ->
                habitStatuses
                    .filter { it.date == LocalDate.now() }
                    .map { it.habitId }
            }.flowOn(Dispatchers.Default)
    }

    override fun getOverallAnalytics(): Flow<OverallAnalytics> {
        return habits.combine(habitStatuses) { habitsFlow, habitStatusesFlow ->
            OverallAnalytics(
                heatMapData = prepareHeatMapData(habitStatusesFlow),
                weekDayFrequencyData = prepareWeekDayFrequencyData(habitStatusesFlow.map { it.date }),
                weeklyGraphData = habitsFlow.associateWith { habit ->
                    val habitStatusesForHabit = habitStatusesFlow.filter { it.habitId == habit.id }

                    prepareLineChartData(
                        firstDay = firstDayOfWeek.value,
                        habitStatuses = habitStatusesForHabit
                    )
                }
            )
        }.flowOn(Dispatchers.Default)
    }

    override fun getHabitsWithStatus(): Flow<List<Pair<Habit, Boolean>>> {
        return habits.combine(habitStatuses) { habitsFlow, statusFlow ->
            habitsFlow.map { habit ->
                val dates = statusFlow
                    .filter { it.habitId == habit.id }
                    .map { it.date }

                habit to dates.any { it == LocalDate.now() }
            }
        }
    }

    override suspend fun getStatusForHabit(id: Long): List<HabitStatus> {
        return habitStatusDao.getStatusForHabit(id).map { it.toHabitStatus() }
    }

    override suspend fun insertHabitStatus(habitStatus: HabitStatus) {
        habitStatusDao.insertHabitStatus(habitStatus.toHabitStatusEntity())

        if (habitStatus.date == LocalDate.now()) {
            notificationManager.cancelNotification(habitId = habitStatus.habitId.toInt())
        }
    }

    override suspend fun deleteHabitStatus(habitId: Long, date: LocalDate) {
        habitStatusDao.deleteStatus(habitId, date)
    }
}