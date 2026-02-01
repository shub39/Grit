package com.shub39.grit.billing

import org.koin.core.annotation.Single

@Single
class BillingHandler {
    suspend fun isPlusUser(): Boolean = true
    suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
}