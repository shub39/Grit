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
package com.shub39.grit.app

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.shub39.grit.billing.BillingInitializer
import com.shub39.grit.di.GritModules
import com.shub39.grit.widgets.alltaskswidget.AllTasksWidgetReceiver
import com.shub39.grit.widgets.habitoverviewwidget.HabitOverviewWidgetReceiver
import com.shub39.grit.widgets.habitstreakwidget.HabitStreakWidgetReceiver
import com.shub39.grit.widgets.habitweekchartwidget.HabitWeekChartWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

class GritApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GritApplication)
            modules(GritModules().module)
        }

        BillingInitializer().initialize(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val manager = GlanceAppWidgetManager(applicationContext)

            @SuppressLint("CheckResult")
            MainScope().launch {
                manager.setWidgetPreviews(HabitOverviewWidgetReceiver::class)
                manager.setWidgetPreviews(HabitStreakWidgetReceiver::class)
                manager.setWidgetPreviews(AllTasksWidgetReceiver::class)
                manager.setWidgetPreviews(HabitWeekChartWidgetReceiver::class)
            }
        }
    }
}
