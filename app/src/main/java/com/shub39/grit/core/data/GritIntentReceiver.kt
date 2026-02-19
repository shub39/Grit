package com.shub39.grit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.core.utils.now
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.ExperimentalTime

class GritIntentReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val TAG = "GritIntentReceiver"
    }

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalTime::class)
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Received intent")
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                val datastore = get<GritDatastore>()
                val pauseNotifications = datastore.getNotificationsFlow().first()

                if (intent != null && !pauseNotifications) {
                    when (intent.action) {
                        IntentActions.HABIT_NOTIFICATION.action -> {
                            Log.d(TAG, "Habit notification intent received")
                            val habitId = intent.getLongExtra("habit_id", -1)
                            if (habitId < 0L) return@launch

                            val habitRepo = get<HabitRepo>()

                            val habit = habitRepo.getHabitById(habitId) ?: return@launch
                            if (!habit.reminder) return@launch

                            // check if habit is completed today, if not then show notification
                            val habitStatus = habitRepo.getStatusForHabit(habitId)

                            if (habitStatus.any { it.date == LocalDate.now() }) {
                                Log.d(TAG, "Habit already completed today")
                            } else {
                                get<GritNotificationManager>().habitNotification(habit)
                            }

                            get<AlarmScheduler>().schedule(habit)
                        }

                        IntentActions.ADD_HABIT_STATUS.action -> {
                            Log.d(TAG, "Add habit status intent received")
                            val habitId = intent.getLongExtra("habit_id", -1)
                            if (habitId < 0) return@launch

                            val habitRepo = get<HabitRepo>()

                            habitRepo.insertHabitStatus(
                                HabitStatus(
                                    habitId = habitId,
                                    date = LocalDate.now()
                                )
                            )

                            Log.d(TAG, "Habit status added successfully")

                            get<GritNotificationManager>().cancelNotification(habitId.toInt())
                        }

                        IntentActions.MARK_TASK_DONE.action -> {
                            Log.d(TAG, "Mark task done intent received")
                            val taskId = intent.getLongExtra("task_id", -1)
                            if (taskId < 0) return@launch

                            val taskRepo = get<TaskRepo>()

                            val task = taskRepo.getTaskById(taskId) ?: return@launch
                            if (task.status || task.reminder == null) return@launch

                            taskRepo.upsertTask(task.copy(status = true, reminder = null))

                            get<GritNotificationManager>().cancelNotification(task)
                        }

                        IntentActions.TASK_NOTIFICATION.action -> {
                            Log.d(TAG, "Task notification intent received")
                            val taskId = intent.getLongExtra("task_id", -1)
                            if (taskId < 0) return@launch

                            val taskRepo = get<TaskRepo>()

                            val task = taskRepo.getTaskById(taskId) ?: return@launch
                            if (!task.status && task.reminder != null) {
                                Log.d(TAG, "sending Task notification")
                                get<GritNotificationManager>().taskNotification(task)
                            }
                        }

                        else -> return@launch
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Error: ", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

}