package com.shub39.grit.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.shub39.grit.R

enum class BottomAppBarDestination(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    TasksPage(R.string.tasks, R.drawable.round_checklist_24, "tasks"),
    HabitsPage(R.string.habits, R.drawable.round_replay_24, "habits"),
    SettingsPage(R.string.settings, R.drawable.round_settings_24, "settings")
}