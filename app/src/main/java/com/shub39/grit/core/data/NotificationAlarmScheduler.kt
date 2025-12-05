package com.shub39.grit.core.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.tasks.domain.Task
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

//implementation of AlarmScheduler using AlarmManager
@OptIn(ExperimentalTime::class)
class NotificationAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val tag = "NotificationAlarmScheduler"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(habit: Habit) {
        cancel(habit)
        if (!habit.reminder) return

        var scheduleTime = habit.time

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        var attempt = 0
        while (scheduleTime < now && attempt <= 365) {
            scheduleTime = LocalDateTime(date = scheduleTime.date.plus(1, DateTimeUnit.DAY), time = scheduleTime.time)
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
            scheduleTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            pendingIntent
        )

        Log.d(
            tag,
            "Scheduled: Habit '${habit.title}' at $scheduleTime"
        )
    }

    override fun schedule(task: Task) {
        cancel(task)
        if (task.reminder == null) return
        val scheduleTime = task.reminder!!

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        if (scheduleTime < now) {
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
            scheduleTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            pendingIntent
        )

        Log.d(
            tag,
            "Scheduled: Task '${task.title}' at $scheduleTime"
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