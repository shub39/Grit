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
package com.shub39.grit.logic.ios

import com.shub39.grit.core.AnalyticsEvent
import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.billing.SubscriptionResult
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.core.interfaces.AppNotificationManager
import com.shub39.grit.core.interfaces.BiometricUtils
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toInstant

private const val HABIT_PREFIX = "habit"
private const val TASK_PREFIX = "task"
private const val SUB_TASK_PREFIX = "subtask"

private fun habitId(habit: Habit) = "$HABIT_PREFIX-${habit.id}"

private fun habitId(habitId: Int) = "$HABIT_PREFIX-$habitId"

private fun taskId(task: Task) = "$TASK_PREFIX-${task.id}"

private fun subTaskId(subTask: SubTask) = "$SUB_TASK_PREFIX-${subTask.id}"

/** Converts an ISO day number (Monday = 1 … Sunday = 7) to the `Calendar` weekday convention. */
private fun Int.toCalendarWeekday(): Int = this % 7 + 1

private fun LocalDateTime.toEpochMillis(): Long =
    toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

internal class IosAlarmScheduler(private val bridge: IosAlarmBridge) : AlarmScheduler {
    override fun schedule(habit: Habit) {
        if (!habit.reminder) return

        bridge.schedule(
            IosReminder(
                id = habitId(habit),
                title = habit.title,
                body = habit.description,
                hour = habit.time.hour,
                minute = habit.time.minute,
                weekdays = habit.days.map { it.isoDayNumber.toCalendarWeekday() }.sorted(),
                fireAtEpochMillis = null,
            )
        )
    }

    override fun schedule(task: Task) {
        val reminder = task.toReminder() ?: return
        bridge.schedule(reminder)
    }

    override fun schedule(subTask: SubTask) {
        val reminder = subTask.toReminder() ?: return
        bridge.schedule(reminder)
    }

    override fun cancel(habit: Habit) = bridge.cancel(habitId(habit))

    override fun cancel(task: Task) = bridge.cancel(taskId(task))

    override fun cancel(subTask: SubTask) = bridge.cancel(subTaskId(subTask))

    override fun cancelAll() = bridge.cancelAll()

    private fun Task.toReminder(): IosReminder? {
        val reminder = reminder ?: return null

        return IosReminder(
            id = taskId(this),
            title = title,
            body = title,
            hour = reminder.hour,
            minute = reminder.minute,
            weekdays = emptyList(),
            fireAtEpochMillis = reminder.toEpochMillis(),
        )
    }

    private fun SubTask.toReminder(): IosReminder? {
        val reminder = reminder ?: return null

        return IosReminder(
            id = subTaskId(this),
            title = title,
            body = title,
            hour = reminder.hour,
            minute = reminder.minute,
            weekdays = emptyList(),
            fireAtEpochMillis = reminder.toEpochMillis(),
        )
    }
}

internal class IosNotificationManager(private val bridge: IosNotificationBridge) :
    AppNotificationManager {
    override fun habitNotification(habit: Habit) {
        bridge.show(habitId(habit), habit.title, habit.description)
    }

    override fun taskNotification(task: Task) {
        bridge.show(taskId(task), task.title, task.title)
    }

    override fun subTaskNotification(subTask: SubTask) {
        bridge.show(subTaskId(subTask), subTask.title, subTask.title)
    }

    override fun cancelNotification(habitId: Int) = bridge.cancel(habitId(habitId))

    override fun cancelNotification(task: Task) = bridge.cancel(taskId(task))

    override fun cancelNotification(subTask: SubTask) = bridge.cancel(subTaskId(subTask))
}

internal class IosBillingHandler(private val bridge: IosBillingBridge) : BillingHandler {
    private val _isPlus = MutableStateFlow(bridge.isPlusUser())

    override val isPlus: StateFlow<Boolean> = _isPlus.asStateFlow()

    /** Called by the app after a StoreKit purchase or restore completes. */
    fun refresh() {
        _isPlus.value = bridge.isPlusUser()
    }

    override suspend fun isPlusUser(): Boolean = bridge.isPlusUser()

    override suspend fun userResult(): SubscriptionResult =
        if (bridge.isPlusUser()) SubscriptionResult.Subscribed else SubscriptionResult.NotSubscribed

    override suspend fun isFoss(): Boolean = bridge.isFoss()
}

internal class IosAnalyticsWrapper(private val bridge: IosAnalyticsBridge) : AnalyticsWrapper {
    override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) {
        bridge.track(event.name, properties.mapValues { it.value.toString() })
    }
}

internal class IosBiometricUtils(private val bridge: IosBiometricBridge) : BiometricUtils {
    override fun getAuthenticators(): Int = bridge.getAuthenticators()

    override fun authenticationAvailable(): Boolean = bridge.authenticationAvailable()
}
