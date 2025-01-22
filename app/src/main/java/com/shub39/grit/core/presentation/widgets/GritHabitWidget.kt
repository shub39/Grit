package com.shub39.grit.core.presentation.widgets

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.Text
import com.shub39.grit.R
import com.shub39.grit.core.presentation.countConsecutiveDaysBeforeLast
import com.shub39.grit.habits.data.database.HabitDatabase
import kotlinx.coroutines.flow.first

class GritWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GritHabitWidget()
}

private val directionKey = ActionParameters.Key<String>("DirectionKey")

class GritHabitWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HabitDatabase.getDatabase(context)
        val habits = database.habitDao().getAllHabits()
        val habitStatuses = database.habitStatusDao().getAllHabitStatuses().first()
        val habitIds = habits.map { it.id }

        provideContent {
            val state = currentState<Preferences>()
            val currentHabitId = state[longPreferencesKey(id.toString())] ?: habitIds.firstOrNull()

            val habit = habits.find { it.id == currentHabitId }
            val dates = habitStatuses.filter { it.habitId == currentHabitId }.map { it.date }

            GlanceTheme {
                Scaffold(
                    titleBar = {
                        TitleBar(
                            title = habit?.title ?: "No Habit",
                            startIcon = ImageProvider(R.drawable.round_alarm_24),
                            actions = {
                                CircleIconButton(
                                    imageProvider = ImageProvider(R.drawable.round_arrow_back_ios_24),
                                    onClick = actionRunCallback<UpdateIndexAction>(
                                        actionParametersOf(
                                            directionKey to "previous"
                                        )
                                    ),
                                    backgroundColor = null,
                                    contentDescription = "Left Arrow",
                                    modifier = GlanceModifier.size(36.dp)
                                )

                                Spacer(modifier = GlanceModifier.size(8.dp))

                                CircleIconButton(
                                    imageProvider = ImageProvider(R.drawable.round_arrow_forward_ios_24),
                                    onClick = actionRunCallback<UpdateIndexAction>(
                                        actionParametersOf(
                                            directionKey to "next"
                                        )
                                    ),
                                    backgroundColor = null,
                                    contentDescription = "Right Arrow",
                                    modifier = GlanceModifier.size(36.dp)
                                )

                                Spacer(modifier = GlanceModifier.size(8.dp))
                            }
                        )
                    }
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.round_local_fire_department_24),
                                contentDescription = "Streak",
                                backgroundColor = null,
                                onClick = {}
                            )

                            Text(
                                text = countConsecutiveDaysBeforeLast(dates).toString()
                            )
                        }
                    }
                }
            }
        }
    }
}

class UpdateIndexAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val database = HabitDatabase.getDatabase(context)
        val habits = database.habitDao().getAllHabits()
        val habitIds = habits.map { it.id }

        val direction = parameters[directionKey] ?: return
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentId = prefs[longPreferencesKey(glanceId.toString())] ?: habitIds.first()
            val currentIndex = habitIds.indexOf(currentId)

            val newIndex = when (direction) {
                "previous" -> (currentIndex - 1).coerceAtLeast(0) // Prevent going out of bounds
                "next" -> (currentIndex + 1).coerceAtMost(habitIds.size - 1) // Prevent overflow
                else -> currentIndex
            }

            prefs[longPreferencesKey(glanceId.toString())] = habitIds[newIndex]
        }

        GritHabitWidget().update(context, glanceId)
    }
}
