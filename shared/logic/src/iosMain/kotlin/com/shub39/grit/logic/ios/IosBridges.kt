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

/**
 * A reminder that the SwiftUI app turns into a `UNNotificationRequest`.
 *
 * Deliberately primitive: Swift implementations never have to deal with `kotlinx.datetime` types,
 * coroutines or flows.
 *
 * @param weekdays repeating days using the `Calendar` convention (1 = Sunday … 7 = Saturday). Empty
 *   for a one-shot reminder, in which case [fireAtEpochMillis] is set instead.
 * @param fireAtEpochMillis absolute fire time in epoch milliseconds, only for one-shot reminders.
 */
class IosReminder(
    val id: String,
    val title: String,
    val body: String,
    val hour: Int,
    val minute: Int,
    val weekdays: List<Int>,
    val fireAtEpochMillis: Long?,
)

/** Implemented in the SwiftUI app, usually on top of `UNUserNotificationCenter`. */
interface IosAlarmBridge {
    fun schedule(reminder: IosReminder)

    fun cancel(id: String)

    fun cancelAll()
}

/** Implemented in the SwiftUI app for in-app / transient notifications. */
interface IosNotificationBridge {
    fun show(id: String, title: String, body: String)

    fun cancel(id: String)
}

/**
 * Implemented in the SwiftUI app on top of StoreKit. Purchase and restore flows live in the UI
 * layer; call `GritSdk.refreshBilling()` once they complete so the shared graph re-reads
 * [isPlusUser].
 */
interface IosBillingBridge {
    fun isPlusUser(): Boolean

    fun isFoss(): Boolean
}

/** Implemented in the SwiftUI app. */
interface IosAnalyticsBridge {
    fun track(event: String, properties: Map<String, String>)
}

/** Implemented in the iOS app, usually on top of `LocalAuthentication`. */
interface IosBiometricBridge {
    fun getAuthenticators(): Int

    fun authenticationAvailable(): Boolean

    /**
     * Shows the system prompt for [reason] and reports the outcome to [onResult]. Implementations
     * must call [onResult] on the main thread.
     */
    fun authenticate(reason: String, onResult: (Boolean) -> Unit)
}

/**
 * The set of platform capabilities the shared graph needs, supplied by the SwiftUI app on
 * [GritSdk.start]. Every bridge defaults to a no-op so the graph still resolves when an
 * implementation is missing.
 */
class IosPlatformServices(
    val alarms: IosAlarmBridge,
    val notifications: IosNotificationBridge,
    val billing: IosBillingBridge,
    val analytics: IosAnalyticsBridge,
    val biometrics: IosBiometricBridge,
) {
    constructor() :
        this(
            alarms = NoopAlarmBridge,
            notifications = NoopNotificationBridge,
            billing = NoopBillingBridge,
            analytics = NoopAnalyticsBridge,
            biometrics = NoopBiometricBridge,
        )
}

internal object NoopAlarmBridge : IosAlarmBridge {
    override fun schedule(reminder: IosReminder) = Unit

    override fun cancel(id: String) = Unit

    override fun cancelAll() = Unit
}

internal object NoopNotificationBridge : IosNotificationBridge {
    override fun show(id: String, title: String, body: String) = Unit

    override fun cancel(id: String) = Unit
}

internal object NoopBillingBridge : IosBillingBridge {
    override fun isPlusUser(): Boolean = false

    override fun isFoss(): Boolean = false
}

internal object NoopAnalyticsBridge : IosAnalyticsBridge {
    override fun track(event: String, properties: Map<String, String>) = Unit
}

internal object NoopBiometricBridge : IosBiometricBridge {
    override fun getAuthenticators(): Int = 0

    override fun authenticationAvailable(): Boolean = false

    override fun authenticate(reason: String, onResult: (Boolean) -> Unit) = onResult(true)
}
