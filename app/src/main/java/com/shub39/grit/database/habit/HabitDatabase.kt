package com.shub39.grit.database.habit

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Habit::class, DailyHabitStatus::class], version = 1)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
