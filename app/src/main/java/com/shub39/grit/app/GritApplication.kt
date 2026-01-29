package com.shub39.grit.app

import android.app.Application
import com.shub39.grit.billing.BillingInitializer
import com.shub39.grit.di.GritModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

class GritApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GritApplication)
            modules(GritModules().module)
        }

        BillingInitializer().initialize(this)
    }

}