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

import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import com.shub39.grit.core.interfaces.AppNotificationManager
import com.shub39.grit.core.interfaces.BiometricUtils
import com.shub39.grit.logic.di.LogicModules
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Binds the app supplied [IosPlatformServices] into the shared graph. Unlike Android and Desktop,
 * nothing here is implemented in Kotlin: every platform capability comes from the app, see
 * [IosBridges].
 *
 * This module only covers the data layer. `:shared:ui` adds the viewmodels on top of it with
 * `loadKoinModules`, because a Koin `@Module` that `includes` a module from another Gradle module
 * emits duplicate hint declarations into both klibs, which Kotlin/Native rejects at link time.
 */
@Module(includes = [LogicModules::class])
class IosModules {
    @Single fun getAlarmScheduler(): AlarmScheduler = IosAlarmScheduler(GritSdk.services.alarms)

    @Single
    fun getNotificationManager(): AppNotificationManager =
        IosNotificationManager(GritSdk.services.notifications)

    @Single fun getBillingHandler(): BillingHandler = GritSdk.billingHandler

    @Single
    fun getAnalyticsWrapper(): AnalyticsWrapper = IosAnalyticsWrapper(GritSdk.services.analytics)

    @Single fun getBiometricUtils(): BiometricUtils = IosBiometricUtils(GritSdk.services.biometrics)
}
