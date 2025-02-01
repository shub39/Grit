package com.shub39.grit.habits.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HabitEntity::class, HabitStatusEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitStatusDao(): HabitStatusDao

    companion object {
        const val DB_NAME = "habit_database"
    }
}
