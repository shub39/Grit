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
import com.shub39.grit.widgets.HabitOverviewWidgetRepository
import com.shub39.grit.widgets.HabitStreakWidgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitStatusDao: HabitStatusDao,
    private val habitOverviewWidgetRepository: HabitOverviewWidgetRepository,
    private val habitHeatMapWidgetRepository: HabitStreakWidgetRepository
): HabitRepo {
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

    override fun getHabitStatus(): Flow<Map<Habit, List<HabitStatus>>> {
        val habits = habitDao.getAllHabitsFlow().map { habits ->
            habits.map { it.toHabit() }.sortedBy { it.index }
        }
        val habitStatuses = habitStatusDao
            .getAllHabitStatuses()
            .map { habitStatuses ->
                habitStatuses.map { it.toHabitStatus() }
            }

        return habits.combine(habitStatuses) { habitsFlow, habitStatusesFlow ->
            habitsFlow.associateWith { habit ->
                habitStatusesFlow.filter { it.habitId == habit.id }
            }
        }
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