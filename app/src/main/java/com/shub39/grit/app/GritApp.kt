package com.shub39.grit.app

import android.app.Application
import com.shub39.grit.billing.BillingInitializer
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

        BillingInitializer().initialize(this)
    }

}