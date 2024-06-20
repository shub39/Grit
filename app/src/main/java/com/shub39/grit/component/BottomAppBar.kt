package com.shub39.grit.component

import androidx.annotation.StringRes
import com.shub39.grit.R

enum class BottomAppBarDestination(
    val direction: String,
    @StringRes val label: Int,
    val iconSelected: Int,
) {
    TodoPage("TodoPage", R.string.tasks, R.drawable.round_checklist_24),
    HabitsPage( "HabitsPage", R.string.habits, R.drawable.round_replay_24),
    AnalyticsPage("AnalyticsPage", R.string.analytics, R.drawable.round_analytics_24),
    SettingsPage("SettingsPage", R.string.settings, R.drawable.round_settings_24),
}