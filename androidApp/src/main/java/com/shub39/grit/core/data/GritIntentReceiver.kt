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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shub39.grit.core.GritLogger
import com.shub39.grit.core.data.notification.GritNotificationManager
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.IntentActions
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.now
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.logic.habits.database.HabitsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GritIntentReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val TAG = "GritIntentReceiver"
    }

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        GritLogger.d(TAG, "Received intent")
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                val datastore = get<SettingsDatastore>()
                val pauseNotifications = datastore.getNotificationsFlow().first()

                if (intent != null && !pauseNotifications) {
                    when (intent.action) {
                        IntentActions.HABIT_NOTIFICATION.action -> habitNotification(intent)

                        IntentActions.ADD_HABIT_STATUS.action -> addHabitStatus(intent)

                        IntentActions.MARK_TASK_DONE.action -> markTaskDone(intent)

                        IntentActions.TASK_NOTIFICATION.action -> taskNotification(intent)

                        else -> return@launch
                    }
                }
            } catch (t: Throwable) {
                GritLogger.e(TAG, "Error: ", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun markTaskDone(intent: Intent) {
        GritLogger.d(TAG, "Mark task done intent received")
        val taskId = intent.getLongExtra("task_id", -1)
        if (taskId < 0) {
            GritLogger.e(TAG, "Invalid Task Id: $taskId")
            return
        }

        val taskRepo = get<TaskRepo>()
        val task = taskRepo.getTaskById(taskId)

        if (task == null) {
            GritLogger.e(TAG, "Invalid Task Id: $taskId")
            return
        }

        taskRepo.upsertTask(task.copy(status = true, reminder = null))

        GritLogger.d(TAG, "Task marked as complete successfully")

        get<GritNotificationManager>().cancelNotification(taskId.toInt())
    }

    private suspend fun addHabitStatus(intent: Intent) {
        GritLogger.d(TAG, "Add habit status intent received")
        val habitId = intent.getLongExtra("habit_id", -1)
        if (habitId < 0 || get<HabitsDao>().getHabitById(habitId) == null) {
            GritLogger.e(TAG, "Invalid Habit Id: $habitId")
            return
        }

        val habitRepo = get<HabitRepo>()

        habitRepo.insertHabitStatus(HabitStatus(habitId = habitId, date = LocalDate.now()))

        GritLogger.d(TAG, "Habit status added successfully")

        get<GritNotificationManager>().cancelNotification(habitId.toInt())
    }

    private suspend fun taskNotification(intent: Intent) {
        GritLogger.d(TAG, "Task notification intent received")
        val taskId = intent.getLongExtra("task_id", -1)
        if (taskId < 0) {
            GritLogger.e(TAG, "Invalid Task Id: $taskId")
            return
        }

        val taskRepo = get<TaskRepo>()

        val task = taskRepo.getTaskById(taskId)

        if (task == null) {
            GritLogger.e(TAG, "Invalid Task Id: $taskId")
            return
        }

        if (!task.status && task.reminder != null) {
            GritLogger.d(TAG, "sending Task notification")
            get<GritNotificationManager>().taskNotification(task)
        }
    }

    private suspend fun habitNotification(intent: Intent) {
        GritLogger.d(TAG, "Habit notification intent received")

        val habitId = intent.getLongExtra("habit_id", -1)
        if (habitId < 0L) {
            GritLogger.e(TAG, "Invalid Habit Id: $habitId")
            return
        }

        val habitRepo = get<HabitRepo>()

        val habit = habitRepo.getHabitById(habitId)

        if (habit == null) {
            GritLogger.e(TAG, "Invalid Habit Id: $habitId")
            return
        }
        if (!habit.reminder) {
            GritLogger.e(TAG, "Reminders are disabled for habit: ${habit.title}")
            return
        }

        // check if habit is completed today, if not then show notification
        val habitStatus = habitRepo.getStatusForHabit(habitId)
        if (habitStatus.any { it.date == LocalDate.now() }) {
            GritLogger.d(TAG, "Habit already completed today")
        } else {
            get<GritNotificationManager>().habitNotification(habit)
        }

        get<AlarmScheduler>().schedule(habit)
    }
}
