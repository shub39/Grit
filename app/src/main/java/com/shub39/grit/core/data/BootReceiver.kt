package com.shub39.grit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.tasks.domain.TaskRepo
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
            val habitRepo = get<HabitRepo>()
            val taskRepo = get<TaskRepo>()
            val pendingResult = goAsync()

            receiverScope.launch {
               try {
                   habitRepo.getHabits().forEach {
                       scheduler.schedule(it)
                       Log.d("BootReceiver", "Scheduled habit: ${it.id}")
                   }

                   taskRepo.getTasks().forEach {
                       scheduler.schedule(it)
                       Log.d("BootReceiver", "Scheduled task: ${it.id}")
                   }
               } catch (t: Throwable) {
                   Log.e("BootReceiver", "Failed to initiate alarms", t)
               } finally {
                   pendingResult.finish()
               }
            }
        }
    }
}