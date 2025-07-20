package com.shub39.grit.billing

class BillingHandler {
    suspend fun isPlusUser(): Boolean = true
    suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
}