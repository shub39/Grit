package com.shub39.grit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.habits.data.database.HabitDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

// reschedules all jobs when device restarts
class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = get<NotificationAlarmScheduler>()
            val habitDao = get<HabitDao>()

            receiverScope.launch {
                habitDao.getAllHabits().forEach {
                    scheduler.schedule(it.toHabit())
                    Log.d("BootReceiver", "Scheduled habit: ${it.id}")
                }
            }
        }
    }
}