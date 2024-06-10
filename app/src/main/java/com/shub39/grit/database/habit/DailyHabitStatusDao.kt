package com.shub39.grit.database.habit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyHabitStatusDao {

    @Query("SELECT * FROM daily_habit_status")
    suspend fun getDailyStatusForHabit(): List<DailyHabitStatus>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStatus(dailyHabitStatus: DailyHabitStatus)

    @Delete
    suspend fun deleteDailyStatus(dailyHabitStatus: DailyHabitStatus)

}