package com.shub39.grit.di

import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.tasks.data.database.TaskDatabase
import com.shub39.grit.habits.data.repository.HabitRepository
import com.shub39.grit.core.domain.NotificationAlarmScheduler
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.tasks.data.repository.TasksRepository
import com.shub39.grit.habits.presentation.HabitViewModel
import com.shub39.grit.tasks.domain.TaskRepo
import com.shub39.grit.tasks.presentation.TaskListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // databases
    single { HabitDatabase.getDatabase(get()) }
    single { TaskDatabase.getDatabase(get()) }

    single { get<HabitDatabase>().habitDao() }
    single { get<HabitDatabase>().habitStatusDao() }
    single { get<TaskDatabase>().taskDao() }
    single { get<TaskDatabase>().categoryDao() }

    singleOf(::HabitRepository).bind<HabitRepo>()
    singleOf(::TasksRepository).bind<TaskRepo>()

    // scheduler
    singleOf(::NotificationAlarmScheduler).bind<AlarmScheduler>()

    // view models
    viewModelOf(::HabitViewModel)
    viewModelOf(::TaskListViewModel)
}