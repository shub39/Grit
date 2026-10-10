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
package com.shub39.grit.di

import com.shub39.grit.core.AnalyticsEvent
import com.shub39.grit.core.GritLogger
import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.billing.SubscriptionResult
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.core.interfaces.AppNotificationManager
import com.shub39.grit.core.interfaces.BiometricUtils
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.logic.di.LogicModules
import com.shub39.grit.shared.ui.di.UIModules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Configuration
@Module(includes = [UIModules::class, LogicModules::class])
@ComponentScan("com.shub39.grit")
class GritModules {
    @Single
    fun provideBiometricUtils(): BiometricUtils =
        object : BiometricUtils {
            override fun getAuthenticators(): Int = 0

            override fun authenticationAvailable(): Boolean = false
        }

    @Single
    fun provideBillingHandler(): BillingHandler =
        object : BillingHandler {
            override val isPlus: StateFlow<Boolean> = MutableStateFlow(true)

            override suspend fun isPlusUser(): Boolean = true

            override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed

            override suspend fun isFoss(): Boolean = true
        }

    @Single
    fun provideAnalyticsWrapper(): AnalyticsWrapper =
        object : AnalyticsWrapper {
            override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) {
                GritLogger.d("Analytics", "${event.name}: $properties")
            }
        }

    @Single
    fun getAlarmScheduler(): AlarmScheduler =
        object : AlarmScheduler {
            override fun schedule(habit: Habit) {
                GritLogger.d("AlarmScheduler", "Scheduled: $habit")
            }

            override fun schedule(task: Task) {
                GritLogger.d("AlarmScheduler", "Scheduled: $task")
            }

            override fun schedule(subTask: SubTask) {
                GritLogger.d("AlarmScheduler", "Scheduled: $subTask")
            }

            override fun cancel(habit: Habit) {
                GritLogger.d("AlarmScheduler", "Cancelled: $habit")
            }

            override fun cancel(task: Task) {
                GritLogger.d("AlarmScheduler", "Cancelled: $task")
            }

            override fun cancel(subTask: SubTask) {
                GritLogger.d("AlarmScheduler", "Cancelled: $subTask")
            }

            override fun cancelAll() {
                GritLogger.d("AlarmScheduler", "Cancelled all alarms")
            }
        }

    @Single
    fun getNotificationManager(): AppNotificationManager =
        object : AppNotificationManager {
            override fun habitNotification(habit: Habit) {
                GritLogger.d("NotificationManager", "Habit Notification: $habit")
            }

            override fun taskNotification(task: Task) {
                GritLogger.d("NotificationManager", "Task Notification: $task")
            }

            override fun cancelNotification(habitId: Int) {
                GritLogger.d("NotificationManager", "Cancel Habit Notification: $habitId")
            }

            override fun cancelNotification(task: Task) {
                GritLogger.d("NotificationManager", "Cancel Task Notification: $task")
            }
        }
}
