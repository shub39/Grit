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
import android.util.Log
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.domain.SettingsDatastore
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
                Log.e(TAG, "Error: ", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun markTaskDone(intent: Intent) {
        Log.d(TAG, "Add habit status intent received")
        val habitId = intent.getLongExtra("habit_id", -1)
        if (habitId < 0) return

        val habitRepo = get<HabitRepo>()

        habitRepo.insertHabitStatus(HabitStatus(habitId = habitId, date = LocalDate.now()))

        Log.d(TAG, "Habit status added successfully")

        get<GritNotificationManager>().cancelNotification(habitId.toInt())
    }

    private suspend fun addHabitStatus(intent: Intent) {
        Log.d(TAG, "Add habit status intent received")
        val habitId = intent.getLongExtra("habit_id", -1)
        if (habitId < 0) return

        val habitRepo = get<HabitRepo>()

        habitRepo.insertHabitStatus(HabitStatus(habitId = habitId, date = LocalDate.now()))

        Log.d(TAG, "Habit status added successfully")

        get<GritNotificationManager>().cancelNotification(habitId.toInt())
    }

    private suspend fun taskNotification(intent: Intent) {
        Log.d(TAG, "Task notification intent received")
        val taskId = intent.getLongExtra("task_id", -1)
        if (taskId < 0) return

        val taskRepo = get<TaskRepo>()

        val task = taskRepo.getTaskById(taskId) ?: return
        if (!task.status && task.reminder != null) {
            Log.d(TAG, "sending Task notification")
            get<GritNotificationManager>().taskNotification(task)
        }
    }

    private suspend fun habitNotification(intent: Intent) {
        Log.d(TAG, "Habit notification intent received")

        val habitId = intent.getLongExtra("habit_id", -1)
        if (habitId < 0L) return

        val habitRepo = get<HabitRepo>()

        val habit = habitRepo.getHabitById(habitId) ?: return
        if (!habit.reminder) return

        // check if habit is completed today, if not then show notification
        val habitStatus = habitRepo.getStatusForHabit(habitId)
        if (habitStatus.any { it.date == LocalDate.now() }) {
            Log.d(TAG, "Habit already completed today")
        } else {
            get<GritNotificationManager>().habitNotification(habit)
        }

        get<AlarmScheduler>().schedule(habit)
    }
}
