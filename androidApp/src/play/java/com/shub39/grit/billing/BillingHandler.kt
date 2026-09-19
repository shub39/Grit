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
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.shub39.grit.core.billing.BillingHandler
import com.shub39.grit.core.billing.SubscriptionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class BillingHandler : BillingHandler {
    companion object {
        private const val ENTITLEMENT_PLUS = "Plus"
    }

    override val isPlus = MutableStateFlow(false)

    private val purchases by lazy { Purchases.sharedInstance }

    init {
        purchases.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            isPlus.update { customerInfo.entitlements.all[ENTITLEMENT_PLUS]?.isActive == true }
        }
    }

    override suspend fun isPlusUser(): Boolean {
        isPlus.update { userResult() is SubscriptionResult.Subscribed }
        return isPlus.value
    }

    override suspend fun userResult(): SubscriptionResult {
        try {
            val userInfo =
                withContext(Dispatchers.IO) {
                    purchases.awaitCustomerInfo(
                        fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT
                    )
                }
            val entitlement = userInfo.entitlements.all[ENTITLEMENT_PLUS]
            val isSubscribed = entitlement?.isActive
            if (isSubscribed == true) {
                isPlus.update { true }
                return SubscriptionResult.Subscribed
            }
        } catch (e: Exception) {
            return SubscriptionResult.Error(e)
        }

        isPlus.update { false }
        return SubscriptionResult.NotSubscribed
    }

    override suspend fun isFoss(): Boolean = false
}
