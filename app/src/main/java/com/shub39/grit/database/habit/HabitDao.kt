package com.shub39.grit.database.habit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM dailyhabitstatus WHERE habitId = :habitId")
    suspend fun getDailyStatusForHabit(habitId: String): List<DailyHabitStatus>

    @Insert
    suspend fun insertHabit(habit: Habit)

    @Insert
    suspend fun insertDailyHabitStatus(status: DailyHabitStatus)


    @Delete
    fun deleteHabit(habit: Habit)

    @Query("DELETE FROM dailyhabitstatus WHERE habitId = :habitId")
    fun deleteDailyStatusForHabit(habitId: String)
}