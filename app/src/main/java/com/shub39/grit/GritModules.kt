package com.shub39.grit

import com.shub39.grit.viewModel.HabitViewModel
import com.shub39.grit.viewModel.TaskListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { HabitViewModel(get()) }
    viewModel { TaskListViewModel(get()) }
}