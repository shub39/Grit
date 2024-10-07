package com.shub39.grit.database.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface HabitStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitStatus(habitStatus: HabitStatus)

    @Query("SELECT * FROM habit_status WHERE habitId = :habitId")
    suspend fun getStatusForHabit(habitId: String): List<HabitStatus>

    @Query("DELETE FROM habit_status WHERE habitId = :habitId AND date = :date")
    suspend fun deleteStatus(habitId: String, date: LocalDate)
}
