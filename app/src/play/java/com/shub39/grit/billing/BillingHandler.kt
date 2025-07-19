package com.shub39.grit.billing

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            val userInfo = withContext(Dispatchers.IO) {
                purchases.awaitCustomerInfo(fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT)
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