package com.shub39.grit.database.habit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Habit::class, HabitStatus::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitStatusDao(): HabitStatusDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
