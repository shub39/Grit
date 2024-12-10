package com.shub39.grit.core.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.habits.data.database.HabitStatusEntity
import com.shub39.grit.tasks.data.database.TaskDatabase
import com.shub39.grit.core.data.mappers.toHabit
import com.shub39.grit.core.presentation.habitNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationReceiver : BroadcastReceiver() {

    private val tag = "NotificationReceiver"
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(tag, "Received intent")
        val scheduler = NotificationAlarmScheduler(context)
        val habitDatabase = HabitDatabase.getDatabase(context)
        val habitStatusDao = habitDatabase.habitStatusDao()

        if (intent != null) {
            when (intent.action) {
                IntentActions.HABIT_NOTIFICATION.action -> {
                    Log.d(tag, "Habit notification received")
                    val habitId = intent.getLongExtra("3", -1)
                    if (habitId < 0L) return
                    val habitTitle = intent.getStringExtra("1") ?: return
                    val habitDescription = intent.getStringExtra("2") ?: return
                    val habitEntity = HabitEntity(
                        id = habitId,
                        title = habitTitle,
                        description = habitDescription,
                        time = LocalDateTime.now()
                    )

                    // check if habit is completed today, if not then shows notification
                    receiverScope.launch {
                        val habitStatus = habitStatusDao.getStatusForHabit(habitId)
                        val time = LocalDateTime.now().toLocalDate()

                        if (
                            habitStatus.any { it.date == time }
                        ) {
                            Log.d(tag, "Habit already completed today")
                        } else {
                            habitNotification(context, habitEntity)
                        }
                    }

                    scheduler.schedule(habitEntity.toHabit())
                }

                IntentActions.TASKS_DELETION.action -> {
                    Log.d(tag, "Tasks deletion received")
                    val taskDatabase = TaskDatabase.getDatabase(context)
                    val preference = intent.getStringExtra("preference") ?: return
                    val taskDao = taskDatabase.taskDao()

                    // deletes all tasks
                    receiverScope.launch {
                        taskDao.deleteAllTasks()
                        Log.d(tag, "Deleted all tasks")
                    }

                    scheduler.schedule(preference)
                }

                IntentActions.ADD_HABIT_STATUS.action -> {
                    Log.d(tag, "Add habit status received")
                    val habitId = intent.getLongExtra("1", -1)
                    if (habitId < 0) return

                    NotificationManagerCompat.from(context).cancel(habitId.hashCode())

                    receiverScope.launch {
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
                }

                else -> return
            }
        }
    }

}