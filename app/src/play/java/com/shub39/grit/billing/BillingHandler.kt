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
package com.shub39.grit.billing

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class BillingHandler {
    companion object {
        private const val ENTITLEMENT_PLUS = "Plus"
    }

    private val purchases by lazy { Purchases.sharedInstance }

    suspend fun isPlusUser(): Boolean {
        return userResult() is SubscriptionResult.Subscribed
    }

    suspend fun userResult(): SubscriptionResult {
        try {
            val userInfo =
                withContext(Dispatchers.IO) {
                    purchases.awaitCustomerInfo(
                        fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT
                    )
                }
            val entitlement = userInfo.entitlements.all[ENTITLEMENT_PLUS]
            val isPlus = entitlement?.isActive
            if (isPlus == true) {
                return SubscriptionResult.Subscribed
            }
        } catch (e: Exception) {
            return SubscriptionResult.Error(e)
        }

        return SubscriptionResult.NotSubscribed
    }
}
