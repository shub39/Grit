package com.shub39.grit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.presentation.habitNotification
import com.shub39.grit.core.presentation.taskNotification
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.tasks.domain.TaskRepo
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
        val habitRepo = get<HabitRepo>()
        val datastore = get<GritDatastore>()

        receiverScope.launch {
            val pauseNotifications = datastore.getNotificationsFlow().first()

            if (intent != null && !pauseNotifications) {
                when (intent.action) {
                    IntentActions.HABIT_NOTIFICATION.action -> {
                        Log.d(tag, "Habit notification received")
                        val habitId = intent.getLongExtra("habit_id", -1)
                        if (habitId < 0L) return@launch
                        val habit = habitRepo.getHabitById(habitId) ?: return@launch

                        // check if habit is completed today, if not then show notification
                        val habitStatus = habitRepo.getStatusForHabit(habitId)
                        val time = LocalDateTime.now().toLocalDate()
                        val todayDayOfWeek = LocalDateTime.now().dayOfWeek

                        if (habitStatus.any { it.date == time }) {
                            Log.d(tag, "Habit already completed today")
                        } else if (!habit.days.contains(todayDayOfWeek)) {
                            Log.d(tag, "Habit is notification not scheduled for today")
                        } else {
                            habitNotification(context, habit)
                        }

                        scheduler.schedule(habit)
                    }

                    IntentActions.ADD_HABIT_STATUS.action -> {
                        Log.d(tag, "Add habit status received")
                        val habitId = intent.getLongExtra("1", -1)
                        if (habitId < 0) return@launch

                        NotificationManagerCompat.from(context).cancel(habitId.toInt())

                        try {
                            val habitStatus = HabitStatus(
                                habitId = habitId,
                                date = LocalDateTime.now().toLocalDate()
                            )
                            habitRepo.insertHabitStatus(habitStatus)

                            Log.d(tag, "Habit status added successfully")
                        } catch (e: Exception) {
                            Log.e(tag, "Error adding habit status", e)
                        }
                    }

                    IntentActions.TASK_NOTIFICATION.action -> {
                        Log.d(tag, "Task notification received")
                        val taskId = intent.getLongExtra("task_id", -1)
                        if (taskId < 0L) return@launch
                        
                        val taskRepo = get<TaskRepo>()
                        val tasks = taskRepo.getTasks()
                        val task = tasks.find { it.id == taskId } ?: return@launch
                        
                        // Don't show notification for completed tasks
                        if (!task.status) {
                            val notificationType = intent.getStringExtra("notification_type") ?: ""
                            taskNotification(context, task, notificationType)
                        }
                    }

                    IntentActions.COMPLETE_TASK.action -> {
                        Log.d(tag, "Complete task received")
                        val taskId = intent.getLongExtra("task_id", -1)
                        if (taskId < 0) return@launch

                        NotificationManagerCompat.from(context).cancel(taskId.toInt())

                        try {
                            val taskRepo = get<TaskRepo>()
                            val tasks = taskRepo.getTasks()
                            val task = tasks.find { it.id == taskId } ?: return@launch
                            
                            task.status = true
                            taskRepo.upsertTask(task)

                            Log.d(tag, "Task marked as complete successfully")
                        } catch (e: Exception) {
                            Log.e(tag, "Error marking task as complete", e)
                        }
                    }

                    else -> return@launch
                }
            }
        }
    }

}