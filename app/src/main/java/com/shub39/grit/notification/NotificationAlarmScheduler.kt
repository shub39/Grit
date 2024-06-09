package com.shub39.grit.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.database.habit.Habit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NotificationAlarmScheduler(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: Habit) {
        if (item.time.isBefore(LocalDateTime.now())) {
            Log.d("NotificationAlarmScheduler", "Notification time is in the past. Aborting")
            return
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("1", item.id)
            putExtra("2", item.description)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.d("NotificationAlarmScheduler", "Scheduled notification for ${item.time.format(DateTimeFormatter.ofPattern("hh:mm a"))}")
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

    override fun repeater() {
        val intent = Intent(context, NotificationReceiver::class.java)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}