package com.shub39.grit.core.presentation.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitStatusEntity

class GritHabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HabitDatabase.getDatabase(context)
        val habit = database.habitDao().getAllHabits().firstOrNull()
        val habitStatuses = habit?.let { database.habitStatusDao().getStatusForHabit(it.id) }

        provideContent {
            GlanceTheme {
                HabitMapWidget(
                    habit = habit,
                    habitStatuses = habitStatuses
                )
            }
        }
    }

    @Composable
    private fun HabitMapWidget(
        habit: HabitEntity?,
        habitStatuses: List<HabitStatusEntity>?
    ) {
        if (habit != null && habitStatuses != null) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(habit.title)
                Text(habit.description)
                Text(habitStatuses.size.toString())
            }
        } else {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Text("Habit Does Not Exist!! :'(")
            }
        }
    }
}