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
package com.shub39.grit.analytics

import com.posthog.PostHog
import com.shub39.grit.BuildConfig
import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.interfaces.AnalyticsWrapper
import kotlin.time.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AnalyticsImpl : AnalyticsWrapper, KoinComponent {
    private fun getDefaultProperties() =
        mapOf(
            "app_name" to "Grit",
            "app_version" to BuildConfig.VERSION_NAME,
            "time_stamp" to Clock.System.now().toEpochMilliseconds().div(1000),
            "is_plus" to get<BillingHandler>().isPlus.value,
        )

    override fun trackEvent(event: String, properties: Map<String, Any>) {
        PostHog.capture(event = event, properties = getDefaultProperties() + properties)
    }
}
