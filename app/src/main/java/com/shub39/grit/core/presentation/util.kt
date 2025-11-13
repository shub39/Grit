package com.shub39.grit.core.presentation

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
import androidx.core.content.ContextCompat.getString
import com.shub39.grit.R
import com.shub39.grit.core.data.NotificationReceiver
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.habits.domain.Habit
import kotlin.random.Random

fun createNotificationChannel(context: Context) {
    val name = getString(context, R.string.channel_name)
    val descriptionText = getString(context, R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("1", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.createNotificationChannel(channel)
}

// shows habit notification if permission granted
fun habitNotification(context: Context, habit: Habit) {
    Log.d("Habit Notification", "Sending Notification")

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("habit_id", habit.id)
        action = IntentActions.ADD_HABIT_STATUS.action
    }
    val pendingBroadcast = PendingIntent.getBroadcast(
        context,
        habit.id.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat
        .Builder(context, "1")
        .setSmallIcon(R.drawable.round_alarm_24)
        .setContentTitle(habit.title)
        .setContentText(habit.description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .addAction(R.drawable.round_check_circle_24, context.getString(R.string.mark_done), pendingBroadcast)

    with(NotificationManagerCompat.from(context)) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notify(habit.id.toInt(), builder.build())

        Log.d("Habit Notification", "Notification sent!")
    }
}

// show task notification if permission granted
fun taskNotification(context: Context, task: Task) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("task_id", task.id)
        action = IntentActions.MARK_TASK_DONE.action
    }
    val pendingBroadcast = PendingIntent.getBroadcast(
        context,
        task.id.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat
        .Builder(context, "1")
        .setSmallIcon(R.drawable.round_checklist_24)
        .setContentTitle(task.title)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .addAction(R.drawable.round_check_circle_24, context.getString(R.string.mark_done), pendingBroadcast)

    with(NotificationManagerCompat.from(context)){
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notify(task.id.toInt(), builder.build())
    }
}

// gotta add this somewhere fr 💯
fun getRandomLine(): String {
    return when(Random.nextInt(0, 10)) {
        1 -> "💣🐊✈ Bombardino Crocodilo"
        2 -> "🌳🦶👃 Brr Brr Patapim"
        3 -> "🐘🌵 Lirili Larila"
        4 -> "😺🦐 Trippi Troppi"
        5 -> "☕🔪 Capucino Assassaino"
        6 -> "😺🐟 Trulimero Trulichina"
        7 -> "💀 Tung Tung Tung Sahur"
        8 -> "🐵🍌 Chimpanzini Bananini"
        9 -> "🦒🍉🌌 Giraffa Celeste"
        else -> "🦈👟 Tralalero Tralala"
    }
}