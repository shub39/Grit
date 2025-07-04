package com.shub39.grit.habits.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_index WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity

    @Query("SELECT * FROM habit_index")
    suspend fun getAllHabits(): List<HabitEntity>

    @Query("SELECT * FROM habit_index")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Upsert
    suspend fun upsertHabit(habitEntity: HabitEntity)

    @Query("DELETE FROM habit_index WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("DELETE FROM habit_index")
    suspend fun deleteAllHabits()
}