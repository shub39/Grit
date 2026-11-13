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

import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.interfaces.ChangelogManager
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.interfaces.ThemeDatastore
import com.shub39.grit.core.settings.backup.ExportRepo
import com.shub39.grit.core.settings.backup.RestoreRepo
import com.shub39.grit.core.tasks.TaskRepo
import org.koin.mp.KoinPlatformTools

/**
 * The shared graph as seen from the Swift app. Called once from `GritApp.init`, before the first
 * Compose frame:
 * ```swift
 * GritSdk.shared.configure(
 *     services: IosPlatformServices(
 *         alarms: LocalAlarmBridge(),
 *         notifications: LocalNotificationBridge(),
 *         billing: StubBillingBridge(),
 *         analytics: ConsoleAnalyticsBridge(),
 *         biometrics: LocalBiometricBridge()
 *     )
 * )
 * ```
 *
 * [configure] only records the platform services; the graph itself is started by
 * `MainViewControllerKt.MainViewController()`, which lives in `:shared:ui` and owns the
 * `@KoinApplication` that lists every module. Every accessor below is only valid after the UI has
 * been created.
 */
object GritSdk {
    /** Read by [IosModules] while building the graph, so it must be set before the UI starts. */
    internal var services: IosPlatformServices = IosPlatformServices()
        private set

    internal var billingHandler: IosBillingHandler = IosBillingHandler(NoopBillingBridge)
        private set

    private val koin
        get() =
            KoinPlatformTools.defaultContext().getOrNull()
                ?: error("The Grit graph is not started yet, create the UI first")

    fun configure(services: IosPlatformServices) {
        this.services = services
        this.billingHandler = IosBillingHandler(services.billing)
    }

    /** Configures no-op platform services, useful for previews and tests. */
    fun configure() = configure(IosPlatformServices())

    /** Re-reads the subscription state from the mutable list of bridges, after a purchase. */
    fun refreshBilling() = billingHandler.refresh()

    /** Whether this device can actually show the biometric prompt. */
    fun biometricsAvailable(): Boolean = services.biometrics.authenticationAvailable()

    /**
     * Shows the system biometric prompt. [onResult] is called on the main thread, so it is safe to
     * update Compose state from it.
     */
    fun authenticate(reason: String, onResult: (Boolean) -> Unit) =
        services.biometrics.authenticate(reason, onResult)

    val habits: HabitRepo
        get() = koin.get()

    val tasks: TaskRepo
        get() = koin.get()

    val themeDatastore: ThemeDatastore
        get() = koin.get()

    val settingsDatastore: SettingsDatastore
        get() = koin.get()

    val exportRepo: ExportRepo
        get() = koin.get()

    val restoreRepo: RestoreRepo
        get() = koin.get()

    val changelogManager: ChangelogManager
        get() = koin.get()
}
