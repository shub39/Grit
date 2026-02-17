package com.shub39.grit.app

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.shub39.grit.billing.BillingInitializer
import com.shub39.grit.di.GritModules
import com.shub39.grit.widgets.all_tasks_widget.AllTasksWidgetReceiver
import com.shub39.grit.widgets.habit_overview_widget.HabitOverviewWidgetReceiver
import com.shub39.grit.widgets.habit_streak_widget.HabitStreakWidgetReceiver
import com.shub39.grit.widgets.habit_weekchart_widget.HabitWeekChartWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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