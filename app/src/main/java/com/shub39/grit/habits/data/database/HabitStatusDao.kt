package com.shub39.grit.habits.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitStatusDao {
    @Query("SELECT * FROM habit_status")
    suspend fun getHabitStatuses(): List<HabitStatusEntity>

    @Query("SELECT * FROM habit_status")
    fun getAllHabitStatuses(): Flow<List<HabitStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitStatus(habitStatusEntity: HabitStatusEntity)

    @Query("SELECT * FROM habit_status WHERE habitId = :habitId")
    suspend fun getStatusForHabit(habitId: Long): List<HabitStatusEntity>

    @Query("DELETE FROM habit_status WHERE habitId = :habitId AND date = :date")
    suspend fun deleteStatus(habitId: Long, date: LocalDate)
}
