package com.shub39.grit.billing

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class BillingInitializer {
    fun initialize(context: Context) {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                context,
                PURCHASES_KEY
            ).build()
        )
    }

    companion object {
        private const val PURCHASES_KEY = "goog_KDirsiVgqVxGhxxgNyxkKZWmLZH"
    }
}