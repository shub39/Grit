package com.shub39.grit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.presentation.habitNotification
import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitStatusDao
import com.shub39.grit.habits.data.database.HabitStatusEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.time.LocalDateTime

class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val tag = "NotificationReceiver"
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(tag, "Received intent")
        val scheduler = get<NotificationAlarmScheduler>()
        val habitStatusDao = get<HabitStatusDao>()
        val datastore = get<GritDatastore>()

        receiverScope.launch {
            val pauseNotifications = datastore.getNotificationsFlow().first()

            if (intent != null && !pauseNotifications) {
                when (intent.action) {
                    IntentActions.HABIT_NOTIFICATION.action -> {
                        Log.d(tag, "Habit notification received")
                        val habitId = intent.getLongExtra("habit_id", -1)
                        if (habitId < 0L) return@launch
                        val habitTitle = intent.getStringExtra("habit_title") ?: return@launch
                        val habitDescription = intent.getStringExtra("habit_description") ?: return@launch
                        val habitEntity = HabitEntity(
                            id = habitId,
                            title = habitTitle,
                            description = habitDescription,
                            time = LocalDateTime.now(),
                            index = 0
                        )

                        // check if habit is completed today, if not then show notification
                        val habitStatus = habitStatusDao.getStatusForHabit(habitId)
                        val time = LocalDateTime.now().toLocalDate()

                        if (habitStatus.any { it.date == time }) {
                            Log.d(tag, "Habit already completed today")
                        } else {
                            habitNotification(context, habitEntity)
                        }

                        scheduler.schedule(habitEntity.toHabit())
                    }

                    IntentActions.ADD_HABIT_STATUS.action -> {
                        Log.d(tag, "Add habit status received")
                        val habitId = intent.getLongExtra("1", -1)
                        if (habitId < 0) return@launch

                        NotificationManagerCompat.from(context).cancel(habitId.toInt())

                        try {
                            val habitStatusEntity = HabitStatusEntity(
                                habitId = habitId,
                                date = LocalDateTime.now().toLocalDate()
                            )
                            habitStatusDao.insertHabitStatus(habitStatusEntity)

                            Log.d(tag, "Habit status added successfully")
                        } catch (e: Exception) {
                            Log.e(tag, "Error adding habit status", e)
                        }
                    }

                    else -> return@launch
                }
            }
        }
    }

}