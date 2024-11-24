package com.shub39.grit.habits.data.repository

import com.shub39.grit.habits.data.database.HabitDao
import com.shub39.grit.habits.data.database.HabitStatusDao
import com.shub39.grit.core.data.mappers.toHabit
import com.shub39.grit.core.data.mappers.toHabitEntity
import com.shub39.grit.core.data.mappers.toHabitStatus
import com.shub39.grit.core.data.mappers.toHabitStatusEntity
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.habits.domain.HabitStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitStatusDao: HabitStatusDao
): HabitRepo {
    override fun getHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabitsFlow().map { entities ->
            entities.map { it.toHabit() }
        }
    }

    override suspend fun upsertHabit(habit: Habit) {
        habitDao.upsertHabit(habit.toHabitEntity())
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
    }

    override fun getHabitStatus(): Flow<Map<Long, List<HabitStatus>>> {
        return habitStatusDao
            .getAllHabitStatuses()
            .map { habitStatuses ->
                habitStatuses.map { it.toHabitStatus() }.groupBy { it.habitId }
            }
    }

    override suspend fun insertHabitStatus(habitStatus: HabitStatus) {
        habitStatusDao.insertHabitStatus(habitStatus.toHabitStatusEntity())
    }

    override suspend fun deleteHabitStatus(id: Long, date: LocalDate) {
        habitStatusDao.deleteStatus(id, date)
    }
}