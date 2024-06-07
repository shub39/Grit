package com.shub39.grit.database.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyHabitStatusDao {

    @Query("SELECT * FROM daily_habits_status WHERE id = :id")
    suspend fun getDailyStatusForHabit(id: String): List<DailyHabitStatus>

    @Insert
    suspend fun insertDailyStatus(dailyHabitStatus: DailyHabitStatus)

    @Update
    suspend fun updateDailyStatus(dailyHabitStatus: DailyHabitStatus)

}