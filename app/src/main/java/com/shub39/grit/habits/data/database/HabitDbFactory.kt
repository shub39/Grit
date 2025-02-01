package com.shub39.grit.habits.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class HabitDbFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<HabitDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(HabitDatabase.DB_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}