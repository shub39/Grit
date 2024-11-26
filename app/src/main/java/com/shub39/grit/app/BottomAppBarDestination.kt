package com.shub39.grit.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.shub39.grit.R

enum class BottomAppBarDestination(
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    TasksPage(R.string.tasks, R.drawable.round_checklist_24),
    HabitsPage(R.string.habits, R.drawable.round_replay_24),
    SettingsPage(R.string.settings, R.drawable.round_settings_24)
}