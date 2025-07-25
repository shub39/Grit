package com.shub39.grit.tasks.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.core.data.NotificationReceiver
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.tasks.domain.Task
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskNotificationScheduler(
    private val context: Context
) {
    private val tag = "TaskNotificationScheduler"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleTaskDeadlineNotifications(task: Task) {
        task.deadline?.let { deadline ->
            // Cancel existing notifications first
            cancelTaskNotifications(task)

            // Don't schedule for completed tasks or past deadlines
            if (task.status || deadline.isBefore(LocalDateTime.now())) {
                return
            }

            // Schedule notifications at different intervals
            scheduleNotification(task, deadline.minusDays(1), NotificationType.ONE_DAY_BEFORE)
            scheduleNotification(task, deadline.withHour(9).withMinute(0), NotificationType.MORNING_OF)
            scheduleNotification(task, deadline.minusHours(2), NotificationType.TWO_HOURS_BEFORE)
        }
    }

    private fun scheduleNotification(task: Task, notificationTime: LocalDateTime, type: NotificationType) {
        // Only schedule if the notification time is in the future
        if (notificationTime.isAfter(LocalDateTime.now())) {
            val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = IntentActions.TASK_NOTIFICATION.action
                putExtra("task_id", task.id)
                putExtra("task_title", task.title)
                putExtra("task_priority", task.priority.name)
                putExtra("notification_type", type.name)
                putExtra("deadline", task.deadline?.atZone(ZoneId.systemDefault())?.toEpochSecond() ?: 0L)
            }

            val requestCode = generateRequestCode(task.id, type)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                pendingIntent
            )

            Log.d(
                tag,
                "Scheduled ${type.name} notification for task '${task.title}' at ${
                    notificationTime.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    )
                }"
            )
        }
    }

    fun cancelTaskNotifications(task: Task) {
        NotificationType.values().forEach { type ->
            val cancelIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = IntentActions.TASK_NOTIFICATION.action
            }

            val requestCode = generateRequestCode(task.id, type)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
        Log.d(tag, "Cancelled all notifications for task '${task.title}'")
    }

    private fun generateRequestCode(taskId: Long, type: NotificationType): Int {
        // Generate unique request code for each notification type
        return (taskId * 10 + type.ordinal).toInt()
    }

    enum class NotificationType {
        ONE_DAY_BEFORE,
        MORNING_OF,
        TWO_HOURS_BEFORE
    }
}