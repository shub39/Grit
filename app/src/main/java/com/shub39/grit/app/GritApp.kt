package com.shub39.grit.app

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.shub39.grit.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin


class GritApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GritApp)
            modules(appModule)
        }

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                this,
                PURCHASES_KEY
            ).build()
        )
    }

    companion object {
        private const val PURCHASES_KEY = "goog_KDirsiVgqVxGhxxgNyxkKZWmLZH"
    }

}