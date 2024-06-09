package com.shub39.grit.database.habit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_index")
    suspend fun getAllHabits(): List<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habit: Habit)
}