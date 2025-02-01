package com.shub39.grit.tasks.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class TaskDbFactory(
    private val context: Context
) {
    fun create() : RoomDatabase.Builder<TaskDatabase> {
        val appContext = context.applicationContext
        val dbfile = appContext.getDatabasePath(TaskDatabase.DB_NAME)

        return Room.databaseBuilder(appContext, dbfile.absolutePath)
    }
}