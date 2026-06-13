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
package com.shub39.grit.core.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shub39.grit.R
import com.shub39.grit.core.data.GritIntentReceiver
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.interfaces.IntentActions
import com.shub39.grit.core.tasks.Task
import org.koin.core.annotation.Single

@Single
class GritNotificationManager(private val context: Context) {
    companion object {
        private const val TAG = "NotificationManager"
        private const val HABIT_NOTIF_ID_OFFSET = 0
        private const val TASK_NOTIF_ID_OFFSET = 1000

        fun createNotificationChannel(context: Context) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel("1", "Grit Notifications", importance).apply {
                    description = "Notification Channel for Habits and Tasks"
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    // shows habit notification if permission granted
    fun habitNotification(habit: Habit) {
        Log.d(TAG, "Sending Habit Notification")

        val intent =
            Intent(context, GritIntentReceiver::class.java).apply {
                putExtra("habit_id", habit.id)
                action = IntentActions.ADD_HABIT_STATUS.action
            }
        val pendingBroadcast =
            PendingIntent.getBroadcast(
                context,
                habit.id.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(habit.title)
                .setContentText(habit.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.notif_icon, "Mark Done", pendingBroadcast)

        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(habit.id.toInt() + HABIT_NOTIF_ID_OFFSET, builder.build())
        } else {
            Log.e(TAG, "Notification permission denied!")
        }
    }

    // show task notification if permission granted
    fun taskNotification(task: Task) {
        val intent =
            Intent(context, GritIntentReceiver::class.java).apply {
                putExtra("task_id", task.id)
                action = IntentActions.MARK_TASK_DONE.action
            }
        val pendingBroadcast =
            PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        val builder =
            NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(task.title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.notif_icon, "Mark Done", pendingBroadcast)

        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(task.id.toInt() + TASK_NOTIF_ID_OFFSET, builder.build())
        } else {
            Log.e(TAG, "Notification permission denied!")
        }
    }

    fun cancelNotification(habitId: Int) {
        notificationManager.cancel(habitId + HABIT_NOTIF_ID_OFFSET)
    }

    fun cancelNotification(task: Task) {
        notificationManager.cancel(task.id.toInt() + TASK_NOTIF_ID_OFFSET)
    }
}
