package com.shub39.grit.di

import com.shub39.grit.database.habit.HabitDatabase
import com.shub39.grit.database.task.TaskDatabase
import com.shub39.grit.notification.NotificationAlarmScheduler
import com.shub39.grit.ui.page.habits_page.HabitViewModel
import com.shub39.grit.ui.page.task_page.TaskListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // databases
    single { HabitDatabase.getDatabase(get()) }
    single { TaskDatabase.getDatabase(get()) }

    // scheduler
    single { NotificationAlarmScheduler(get()) }

    // viewmodels
    viewModel { HabitViewModel(get(), get()) }
    viewModel { TaskListViewModel(get(), get()) }
}