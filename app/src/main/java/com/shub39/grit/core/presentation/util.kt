package com.shub39.grit.core.presentation

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.data.NotificationReceiver
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.habits.domain.Habit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
fun timePickerStateToLocalDateTime(timePickerState: TimePickerState, date: LocalDate = LocalDate.now()): LocalDateTime {
    return LocalDateTime.of(date, java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
}

fun countCurrentStreak(dates: List<LocalDate>): Int {
    if (dates.isEmpty()) return 0

    val today = LocalDate.now()
    val sortedDates = dates.sorted()
    val lastDate = sortedDates.last()

    if (ChronoUnit.DAYS.between(lastDate, today) > 1L) {
        return 0
    }

    var streak = 1

    for (i in sortedDates.size - 2 downTo 0) {
        val currentDate = sortedDates[i]
        val nextDate = sortedDates[i + 1]

        if (ChronoUnit.DAYS.between(currentDate, nextDate) == 1L) {
            streak++
        } else {
            break
        }
    }

    return streak
}


fun countBestStreak(dates: List<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    val sortedDates = dates.sorted()
    var maxConsecutive = 0
    var currentConsecutive = 0
    for (i in 1 until sortedDates.size) {
        val previousDate = sortedDates[i - 1]
        val currentDate = sortedDates[i]
        if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
            currentConsecutive++
        } else {
            if (currentConsecutive > maxConsecutive) {
                maxConsecutive = currentConsecutive
            }
            currentConsecutive = 1
        }
    }
    if (currentConsecutive > maxConsecutive) {
        maxConsecutive = currentConsecutive
    }
    return maxConsecutive
}

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

// shows notification when habit is added if permission is granted. otherwise requests permission
fun showAddNotification(context: Context, habit: Habit) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val builder = NotificationCompat.Builder(context, "1")
        .setSmallIcon(R.drawable.round_checklist_24)
        .setContentTitle(getString(context, R.string.new_habit) + " " + habit.title)
        .setContentText(
            getString(context, R.string.at) + " " + habit.time.format(
                DateTimeFormatter.ofPattern("hh:mm a")
            )
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), // works fine in API 29 :/
                1
            )
            return
        }

        notify(habit.id.hashCode(), builder.build())
    }
}

// shows habit notification if permission granted
fun habitNotification(context: Context, habit: Habit) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("1", habit.id)
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
        .setSmallIcon(R.drawable.round_checklist_24)
        .setContentTitle(habit.title)
        .setContentText(habit.description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .addAction(R.drawable.round_check_circle_24, "Mark Done", pendingBroadcast)

    with(NotificationManagerCompat.from(context)) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notify(habit.id.hashCode(), builder.build())
    }
}

fun getRandomLine(): String {
    return when(Random.nextInt(0, 10)) {
        1 -> "\uD83D\uDCA3\uFE0F Bombardino Crocodilo"
        2 -> "\uD83C\uDF33 Brr Brr Patapim"
        3 -> "\uD83C\uDF35 Lirili Larila"
        4 -> "\uD83D\uDE3A Trippi Troppi"
        5 -> "☕\uFE0F Capucino Assassaino"
        6 -> "\uD83D\uDC1F\uFE0F Trulimero Trulichina"
        7 -> "\uD83D\uDC80 Tung Tung Tung Sahur"
        8 -> "\uD83E\uDD8D Chimpanzini Bananini"
        9 -> "\uD83E\uDD92 Giraffa Celeste"
        else -> "\uD83E\uDD88 Tralalero Tralala"
    }
}