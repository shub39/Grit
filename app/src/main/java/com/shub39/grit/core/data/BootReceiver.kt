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
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.tasks.domain.TaskRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

// reschedules all jobs when device restarts
class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = get<NotificationAlarmScheduler>()
            val habitRepo = get<HabitRepo>()
            val taskRepo = get<TaskRepo>()
            val pendingResult = goAsync()

            receiverScope.launch {
                try {
                    habitRepo.getHabits().forEach {
                        scheduler.schedule(it)
                        Log.d("BootReceiver", "Scheduled habit: ${it.id}")
                    }

                    taskRepo.getTasks().forEach {
                        scheduler.schedule(it)
                        Log.d("BootReceiver", "Scheduled task: ${it.id}")
                    }
                } catch (t: Throwable) {
                    Log.e("BootReceiver", "Failed to initiate alarms", t)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
