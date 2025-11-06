package com.shub39.grit.habits.data.repository

import com.shub39.grit.core.data.toHabit
import com.shub39.grit.core.data.toHabitEntity
import com.shub39.grit.core.data.toHabitStatus
import com.shub39.grit.core.data.toHabitStatusEntity
import com.shub39.grit.habits.data.database.HabitDao
import com.shub39.grit.habits.data.database.HabitStatusDao
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.domain.HabitWithAnalytics
import com.shub39.grit.habits.domain.OverallAnalytics
import com.shub39.grit.widgets.HabitOverviewWidgetRepository
import com.shub39.grit.widgets.HabitStreakWidgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitStatusDao: HabitStatusDao,
    private val habitOverviewWidgetRepository: HabitOverviewWidgetRepository,
    private val habitHeatMapWidgetRepository: HabitStreakWidgetRepository
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

    override suspend fun upsertHabit(habit: Habit) {
        habitDao.upsertHabit(habit.toHabitEntity())
        habitOverviewWidgetRepository.update()
        habitHeatMapWidgetRepository.update()
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        habitOverviewWidgetRepository.update()
        habitHeatMapWidgetRepository.update()
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

    override fun getHabitStatus(firstDayOfWeek: DayOfWeek): Flow<List<HabitWithAnalytics>> {
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
                        firstDay = firstDayOfWeek,
                        habitStatuses = habitStatusesForHabit
                    ),
                    weekDayFrequencyData = prepareWeekDayFrequencyData(dates = dates),
                    startedDaysAgo = ChronoUnit.DAYS.between(
                        habit.time.toLocalDate(),
                        LocalDate.now()
                    )
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override fun getOverallAnalytics(firstDayOfWeek: DayOfWeek): Flow<OverallAnalytics> {
        return habits.combine(habitStatuses) { habitsFlow, habitStatusesFlow ->
            OverallAnalytics(
                heatMapData = prepareHeatMapData(habitStatusesFlow),
                weekDayFrequencyData = prepareWeekDayFrequencyData(habitStatusesFlow.map { it.date }),
                weeklyGraphData = habitsFlow.associateWith { habit ->
                    val habitStatusesForHabit = habitStatusesFlow.filter { it.habitId == habit.id }

                    prepareLineChartData(
                        firstDay = firstDayOfWeek,
                        habitStatuses = habitStatusesForHabit
                    )
                }
            )
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun getStatusForHabit(id: Long): List<HabitStatus> {
        return habitStatusDao.getStatusForHabit(id).map { it.toHabitStatus() }
    }

    override suspend fun insertHabitStatus(habitStatus: HabitStatus) {
        habitStatusDao.insertHabitStatus(habitStatus.toHabitStatusEntity())
        habitHeatMapWidgetRepository.update()

        if (habitStatus.date == LocalDate.now()) {
            habitOverviewWidgetRepository.update()
        }
    }

    override suspend fun deleteHabitStatus(id: Long, date: LocalDate) {
        habitStatusDao.deleteStatus(id, date)
        habitHeatMapWidgetRepository.update()

        if (date == LocalDate.now()) {
            habitOverviewWidgetRepository.update()
        }
    }
}