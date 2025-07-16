package com.shub39.grit.di

import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.core.data.DataStoreImpl
import com.shub39.grit.core.data.DatastoreFactory
import com.shub39.grit.core.data.NotificationAlarmScheduler
import com.shub39.grit.core.data.backup.export.ExportImpl
import com.shub39.grit.core.data.backup.restore.RestoreImpl
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.habits.data.database.HabitDbFactory
import com.shub39.grit.habits.data.repository.HabitRepository
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.tasks.data.database.TaskDatabase
import com.shub39.grit.tasks.data.database.TaskDbFactory
import com.shub39.grit.tasks.data.repository.TasksRepository
import com.shub39.grit.tasks.domain.TaskRepo
import com.shub39.grit.viewmodels.HabitViewModel
import com.shub39.grit.viewmodels.MainViewModel
import com.shub39.grit.viewmodels.SettingsViewModel
import com.shub39.grit.viewmodels.StateLayer
import com.shub39.grit.viewmodels.TasksViewModel
import com.shub39.grit.widgets.HabitOverviewWidgetRepository
import com.shub39.grit.widgets.HabitStreakWidgetRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // databases
    singleOf(::HabitDbFactory)
    singleOf(::TaskDbFactory)
    single {
        get<HabitDbFactory>()
            .create()
            .build()
    }
    single {
        get<TaskDbFactory>()
            .create()
            .build()
    }

    single { get<HabitDatabase>().habitDao() }
    single { get<HabitDatabase>().habitStatusDao() }
    single { get<TaskDatabase>().taskDao() }
    single { get<TaskDatabase>().categoryDao() }

    singleOf(::HabitRepository).bind<HabitRepo>()
    singleOf(::TasksRepository).bind<TaskRepo>()

    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()

    // Datastore
    singleOf(::DatastoreFactory)
    single { get<DatastoreFactory>().getPreferencesDataStore() }
    singleOf(::DataStoreImpl).bind<GritDatastore>()

    // scheduler
    singleOf(::NotificationAlarmScheduler).bind<AlarmScheduler>()

    // widget repositories
    singleOf(::HabitOverviewWidgetRepository)
    singleOf(::HabitStreakWidgetRepository)

    // Revenuecat
    singleOf(::BillingHandler)

    // view models
    singleOf(::StateLayer)
    viewModelOf(::MainViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::HabitViewModel)
    viewModelOf(::TasksViewModel)
}