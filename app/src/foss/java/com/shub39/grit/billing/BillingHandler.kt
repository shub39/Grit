package com.shub39.grit.billing

class BillingHandler {
    suspend fun isPlusUser(): Boolean = false
    suspend fun userResult(): SubscriptionResult = SubscriptionResult.NotSubscribed
}