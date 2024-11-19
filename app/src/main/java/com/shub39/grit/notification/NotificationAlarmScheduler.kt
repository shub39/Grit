package com.shub39.grit.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.logic.OtherLogic.getNextMonday
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NotificationAlarmScheduler(
    private val context: Context
): AlarmScheduler {

    private val tag = "NotificationAlarmScheduler"
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    // schedule notifications for habit
    override fun schedule(item: Habit) {
        var time = item.time

        while (time.isBefore(LocalDateTime.now())) {
            time = time.plusDays(1)
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("1", item.title)
            putExtra("2", item.description)
            putExtra("3", item.id)
            action = IntentActions.HABIT_NOTIFICATION.action
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d(tag, "Scheduled notification for ${time.format(DateTimeFormatter.ofPattern("dd/MM/ hh:mm a"))}")
    }

    // schedule deletion of tasks
    override fun schedule(preference: String) {
        // sets the time to preference
        val time = when (preference) {
            "Daily" -> LocalDateTime.now().plusDays(1).withHour(0).withMinute(0)
            "Weekly" -> getNextMonday()
            "Monthly" -> LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0)
            else -> return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("preference", preference)
            action = IntentActions.TASKS_DELETION.action
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                preference.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d(tag, "Set delete time: ${time.format(DateTimeFormatter.ofPattern("dd/MM/ hh:mm a"))}")
    }

    // cancel habit notifications
    override fun cancel(item: Habit) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d(tag, "Cancelled notification for ${item.title}")
    }

    // cancel task deletion
    override fun cancel(preference: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                preference.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d(tag, "Cancelled notification for $preference")
    }

}