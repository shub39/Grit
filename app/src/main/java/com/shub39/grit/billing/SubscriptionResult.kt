package com.shub39.grit.billing


sealed interface SubscriptionResult {
    data object Subscribed : SubscriptionResult
    data object NotSubscribed : SubscriptionResult
    data class Error(val e: Throwable) : SubscriptionResult
}