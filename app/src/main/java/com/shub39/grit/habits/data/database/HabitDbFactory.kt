package com.shub39.grit.habits.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class HabitDbFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<HabitDatabase> {
        val appContext = context.applicationContext

        return Room.databaseBuilder(appContext, HabitDatabase::class.java, HabitDatabase.DB_NAME)
            .addMigrations(HabitDatabase.migrate_3_4)
    }
}