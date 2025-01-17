package com.shub39.grit.core.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.core.data.mappers.toHabit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// reschedules all jobs when device restarts
class BootReceiver : BroadcastReceiver() {
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = NotificationAlarmScheduler(context)
            val habitDatabase = HabitDatabase.getDatabase(context)
            val habitDao = habitDatabase.habitDao()

            receiverScope.launch {
                habitDao.getAllHabits().forEach {
                    scheduler.schedule(it.toHabit())
                    Log.d("BootReceiver", "Scheduled habit: ${it.id}")
                }
            }
        }
    }

}