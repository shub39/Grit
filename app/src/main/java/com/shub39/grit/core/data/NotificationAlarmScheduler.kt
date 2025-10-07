package com.shub39.grit.core.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.tasks.domain.Task
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

//implementation of AlarmScheduler using AlarmManager
class NotificationAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val tag = "NotificationAlarmScheduler"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(habit: Habit) {
        cancel(habit)
        if (!habit.reminder) return
        var scheduleTime = habit.time

        var attempt = 0
        while (scheduleTime.isBefore(LocalDateTime.now()) && attempt < 365) {
            scheduleTime = scheduleTime.plusDays(1).withSecond(0)
            attempt++
        }

        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.HABIT_NOTIFICATION.action
            putExtra("habit_id", habit.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            scheduleTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            pendingIntent
        )

        Log.d(
            tag,
            "Scheduled: Habit '${habit.title}' at ${
                scheduleTime.format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy hh:mm a",
                        Locale.getDefault()
                    )
                )
            }"
        )
    }

    override fun schedule(task: Task) {
        cancel(task)
        if (task.reminder == null) return
        val scheduleTime = task.reminder

        if (scheduleTime.isBefore(LocalDateTime.now())) {
            Log.d(tag, "Task '${task.title}' reminder time is in the past")
            return
        }

        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.TASK_NOTIFICATION.action
            putExtra("task_id", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            scheduleTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            pendingIntent
        )

        Log.d(
            tag,
            "Scheduled: Task '${task.title}' at ${
                scheduleTime.format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy hh:mm a",
                        Locale.getDefault()
                    )
                )
            }"
        )
    }

    override fun cancel(habit: Habit) {
        val cancelIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.HABIT_NOTIFICATION.action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.toInt(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(tag, "Cancelled: Habit '${habit.title}'")
    }

    override fun cancel(task: Task) {
        val cancelIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.TASK_NOTIFICATION.action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(tag, "Cancelled: Task '${task.title}'")
    }

    override fun cancelAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll()
        }
    }

}