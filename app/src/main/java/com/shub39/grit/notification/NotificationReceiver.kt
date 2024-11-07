package com.shub39.grit.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.database.task.TaskDatabase
import com.shub39.grit.notification.NotificationMethods.habitNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("NotificationReceiver", "Received")
        val scheduler = NotificationAlarmScheduler(context)
        if (intent != null) {
            if (intent.getStringExtra("preference") == null) {
                val habitId = intent.getStringExtra("1") ?: return
                val habitDescription = intent.getStringExtra("2") ?: return

                val habit = Habit(
                    title = habitId,
                    description = habitDescription,
                    time = LocalDateTime.now()
                )
                habitNotification(context, habit)
                scheduler.schedule(habit)
            } else {
                val taskDatabase = TaskDatabase.getDatabase(context)
                val preference = intent.getStringExtra("preference") ?: return
                val taskDao = taskDatabase.taskDao()
                receiverScope.launch {
                    taskDao.deleteAllTasks()
                    Log.d("NotificationReceiver", "Deleted all tasks")
                }
                scheduler.schedule(preference)
            }
        }
    }

}