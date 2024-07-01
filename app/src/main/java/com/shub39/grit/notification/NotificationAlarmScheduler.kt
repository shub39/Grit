package com.shub39.grit.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.database.habit.Habit
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NotificationAlarmScheduler(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: Habit) {
        var time = item.time
        while (time.isBefore(LocalDateTime.now())) {
            time = time.plusDays(1)
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("1", item.id)
            putExtra("2", item.description)
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
        Log.d("NotificationAlarmScheduler", "Scheduled notification for ${time.format(DateTimeFormatter.ofPattern("dd/MM/ hh:mm a"))}")
    }

    override fun schedule(preference: String) {
        val time = when (preference) {
            "Daily" -> LocalDateTime.now().plusDays(1).withHour(0).withMinute(0)
            "Weekly" -> getNextMonday()
            "Monthly" -> LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0)
            else -> return
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("preference", preference)
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
        Log.d("Set delete time", time.format(DateTimeFormatter.ofPattern("dd/MM/ hh:mm a")))
    }

    override fun cancel(item: Habit) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(preference: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                preference.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun getNextMonday(): LocalDateTime {
        var nextMonday = LocalDateTime.now().plusWeeks(1)
        while (nextMonday.dayOfWeek != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.minusDays(1)
        }
        return nextMonday.withHour(0).withMinute(0)
    }

}