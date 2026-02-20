/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import com.shub39.grit.core.utils.now
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import org.koin.core.annotation.Single

// implementation of AlarmScheduler using AlarmManager
@Single(binds = [AlarmScheduler::class])
@OptIn(ExperimentalTime::class)
class NotificationAlarmScheduler(private val context: Context) : AlarmScheduler {

    companion object {
        private const val TAG = "NotificationAlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(habit: Habit) {
        cancel(habit)
        if (!habit.reminder || habit.days.isEmpty()) return

        var scheduleTime = habit.time
        val now = LocalDateTime.now()

        while ((scheduleTime < now) || !habit.days.contains(scheduleTime.dayOfWeek)) {
            scheduleTime =
                scheduleTime.date.plus(1, DateTimeUnit.DAY).let {
                    LocalDateTime(date = it, time = scheduleTime.time)
                }
        }

        val notificationIntent =
            Intent(context, GritIntentReceiver::class.java).apply {
                action = IntentActions.HABIT_NOTIFICATION.action
                putExtra("habit_id", habit.id)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                habit.id.toInt(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            scheduleTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            pendingIntent,
        )

        Log.d(TAG, "Scheduled: Habit '${habit.title}' at $scheduleTime")
    }

    override fun schedule(task: Task) {
        cancel(task)
        if (task.reminder == null) return
        val scheduleTime = task.reminder!!

        val now = LocalDateTime.now()

        if (scheduleTime < now) {
            Log.d(TAG, "Task '${task.title}' reminder time is in the past")
            return
        }

        val notificationIntent =
            Intent(context, GritIntentReceiver::class.java).apply {
                action = IntentActions.TASK_NOTIFICATION.action
                putExtra("task_id", task.id)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            scheduleTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            pendingIntent,
        )

        Log.d(TAG, "Scheduled: Task '${task.title}' at $scheduleTime")
    }

    override fun cancel(habit: Habit) {
        val cancelIntent =
            Intent(context, GritIntentReceiver::class.java).apply {
                action = IntentActions.HABIT_NOTIFICATION.action
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                habit.id.toInt(),
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled: Habit '${habit.title}'")
    }

    override fun cancel(task: Task) {
        val cancelIntent =
            Intent(context, GritIntentReceiver::class.java).apply {
                action = IntentActions.TASK_NOTIFICATION.action
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled: Task '${task.title}'")
    }

    override fun cancelAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll()
        }
    }
}
