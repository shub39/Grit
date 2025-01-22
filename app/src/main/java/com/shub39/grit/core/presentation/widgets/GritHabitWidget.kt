package com.shub39.grit.core.presentation.widgets

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.absolutePadding
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.core.presentation.countBestStreak
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.data.database.HabitDatabase
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

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

            val containerColor = GlanceTheme.colors.secondaryContainer
            val contentColor = GlanceTheme.colors.onSecondaryContainer

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
                                    imageProvider = ImageProvider(R.drawable.round_replay_24),
                                    onClick = actionRunCallback<UpdateIndexAction>(
                                        actionParametersOf(
                                            directionKey to "current"
                                        )
                                    ),
                                    backgroundColor = null,
                                    contentDescription = "Replay",
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
                        modifier = GlanceModifier
                            .absolutePadding(bottom = 12.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = GlanceModifier
                                .background(containerColor)
                                .cornerRadius(12.dp)
                                .defaultWeight()
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.round_local_fire_department_24),
                                contentDescription = "Streak",
                                modifier = GlanceModifier.size(42.dp),
                                contentColor = contentColor,
                                backgroundColor = null,
                                onClick = {}
                            )

                            Text(
                                text = countCurrentStreak(dates).toString(),
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = context.getString(R.string.streak).split(" ").joinToString(separator = "\n"),
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Spacer(modifier = GlanceModifier.size(12.dp))

                        Column(
                            modifier = GlanceModifier
                                .background(containerColor)
                                .cornerRadius(12.dp)
                                .defaultWeight()
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.round_local_fire_department_24),
                                contentDescription = "Streak",
                                contentColor = contentColor,
                                modifier = GlanceModifier.size(42.dp),
                                backgroundColor = null,
                                onClick = {}
                            )

                            Text(
                                text = countBestStreak(dates).toString(),
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = context.getString(R.string.best_streak).split(" ").joinToString(separator = "\n"),
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Spacer(modifier = GlanceModifier.size(12.dp))

                        Column(
                            modifier = GlanceModifier
                                .background(containerColor)
                                .cornerRadius(12.dp)
                                .defaultWeight()
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.baseline_flag_circle_24),
                                contentDescription = "Flag",
                                contentColor = contentColor,
                                modifier = GlanceModifier.size(42.dp),
                                backgroundColor = null,
                                onClick = {}
                            )

                            Text(
                                text = if (habit != null) {
                                    habit.time.format(DateTimeFormatter.ofPattern("d"))
                                } else {"???"},
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = if (habit != null) {
                                    habit.time.format(DateTimeFormatter.ofPattern("MMM\nuuuu"))
                                } else {"???"},
                                style = TextStyle(
                                    color = contentColor,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
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
                "previous" -> (currentIndex - 1).coerceAtLeast(0)
                "next" -> (currentIndex + 1).coerceAtMost(habitIds.size - 1)
                else -> currentIndex
            }

            prefs[longPreferencesKey(glanceId.toString())] = habitIds[newIndex]
        }

        GritHabitWidget().update(context, glanceId)
    }
}
