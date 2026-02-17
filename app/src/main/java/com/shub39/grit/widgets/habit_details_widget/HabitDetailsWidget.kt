package com.shub39.grit.widgets.habit_details_widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.utils.now
import com.shub39.grit.widgets.WidgetSize
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HabitDetailsWidget : GlanceAppWidget(), KoinComponent {

    companion object {
        private val habitIdKey = longPreferencesKey("habit_id")
    }

    override val sizeMode: SizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<Preferences> =
        PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repo = get<HabitRepo>()

        provideContent {
            val scope = rememberCoroutineScope()
            val size = LocalSize.current

            val habitsWithAnalytics by repo.getHabitsWithAnalytics().collectAsState(emptyList())
            val sortedData = habitsWithAnalytics.sortedBy { it.habit.id }

            val state = currentState<Preferences>()
            val habitId = state[habitIdKey] ?: sortedData.firstOrNull()?.habit?.id ?: 0

            val currentData = sortedData.find { it.habit.id == habitId } ?: sortedData.firstOrNull()
            val currentIndex = if (currentData == null) 0 else sortedData.indexOf(currentData)

            key(size) {
                GlanceTheme {
                    Content(
                        habitWithAnalytics = currentData,
                        onUpdateWidget = {
                            scope.launch {
                                this@HabitDetailsWidget.update(context, id)
                            }
                        },
                        onChangeHabit = {
                            val nextId = if (currentIndex == sortedData.size - 1) {
                                sortedData.first().habit.id
                            } else {
                                sortedData[currentIndex + 1].habit.id
                            }

                            scope.launch {
                                updateHabitId(context, id, nextId)
                            }
                        }
                    )
                }
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            Content(
                onUpdateWidget = {},
                onChangeHabit = {},
                habitWithAnalytics = HabitWithAnalytics(
                    habit = Habit(
                        id = 1,
                        title = "Exercise",
                        description = "40 mins daily",
                        time = LocalDateTime.now(),
                        days = setOf(),
                        index = 1,
                        reminder = false
                    ),
                    statuses = listOf(),
                    weeklyComparisonData = listOf(),
                    weekDayFrequencyData = mapOf(),
                    currentStreak = 12,
                    bestStreak = 20,
                    startedDaysAgo = 100
                )
            )
        }
    }

    suspend fun updateHabitId(
        context: Context,
        glanceId: GlanceId,
        newHabitId: Long
    ) {
        updateAppWidgetState(context, glanceId) {
            it[habitIdKey] = newHabitId
        }
        update(context, glanceId)
    }
}

@GlanceComposable
@Composable
private fun Content(
    modifier: GlanceModifier = GlanceModifier,
    habitWithAnalytics: HabitWithAnalytics?,
    onUpdateWidget: () -> Unit,
    onChangeHabit: () -> Unit
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val roundedCornerSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (roundedCornerSupported) {
                    GlanceModifier
                        .background(GlanceTheme.colors.widgetBackground)
                        .cornerRadius(24.dp)
                } else {
                    GlanceModifier.background(
                        imageProvider = ImageProvider(R.drawable.rounded_4dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground)
                    )
                }
            )
            .clickable(actionStartActivity<MainActivity>())
    ) {
        if (habitWithAnalytics != null) {
            TitleBar(
                startIcon = ImageProvider(R.drawable.analytics),
                title = habitWithAnalytics.habit.title,
                actions = {
                    if (size.width >= WidgetSize.Width4) {
                        Box(GlanceModifier.padding(start = 16.dp)) {
                            Image(
                                provider = ImageProvider(R.drawable.refresh),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                modifier = GlanceModifier.clickable { onUpdateWidget() }
                            )
                        }
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Box(GlanceModifier.padding(end = 16.dp)) {
                            Image(
                                provider = ImageProvider(R.drawable.arrow_forward),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                modifier = GlanceModifier.clickable {
                                    onChangeHabit()
                                    onUpdateWidget()
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = GlanceModifier.width(16.dp))
                    }
                }
            )

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                // current streak stat
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .then(
                            if (roundedCornerSupported) {
                                GlanceModifier
                                    .cornerRadius(16.dp)
                                    .background(GlanceTheme.colors.primary)
                            } else {
                                GlanceModifier
                                    .background(
                                        imageProvider = ImageProvider(R.drawable.rounded_16dp),
                                        colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                                    )
                            }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.heat),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary),
                            modifier = GlanceModifier.size(32.dp)
                        )

                        if (size.width >= WidgetSize.Width4) {
                            Spacer(modifier = GlanceModifier.width(4.dp))

                            Row {
                                Text(
                                    text = "${habitWithAnalytics.currentStreak} Day ",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )

                                Text(
                                    text = "Current Streak",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )
                            }
                        } else {
                            Spacer(modifier = GlanceModifier.width(4.dp))

                            Column {
                                Text(
                                    text = "${habitWithAnalytics.currentStreak} Day ",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )

                                Text(
                                    text = "Current Streak",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                // best streak stat
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .then(
                            if (roundedCornerSupported) {
                                GlanceModifier
                                    .cornerRadius(16.dp)
                                    .background(GlanceTheme.colors.secondary)
                            } else {
                                GlanceModifier
                                    .background(
                                        imageProvider = ImageProvider(R.drawable.rounded_16dp),
                                        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary)
                                    )
                            }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.heat_outlined),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary),
                            modifier = GlanceModifier.size(32.dp)
                        )

                        if (size.width >= WidgetSize.Width4) {
                            Spacer(modifier = GlanceModifier.width(4.dp))

                            Row {
                                Text(
                                    text = "${habitWithAnalytics.bestStreak} Day ",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp,
                                        color = GlanceTheme.colors.onSecondary
                                    )
                                )

                                Text(
                                    text = "Best Streak",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onSecondary
                                    )
                                )
                            }
                        } else {
                            Spacer(modifier = GlanceModifier.width(4.dp))

                            Column {
                                Text(
                                    text = "${habitWithAnalytics.bestStreak} Day ",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )

                                Text(
                                    text = "Best Streak",
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onPrimary
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }
        } else {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.nothing_to_show),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(heightDp = 200, widthDp = 240)
@Preview(heightDp = 200, widthDp = 300)
@Composable
private fun GlancePreview() {
    Content(
        onUpdateWidget = {},
        onChangeHabit = {},
        habitWithAnalytics = HabitWithAnalytics(
            habit = Habit(
                id = 1,
                title = "Test Habit",
                description = "A Test Habit",
                time = LocalDateTime.now(),
                days = setOf(),
                index = 1,
                reminder = false
            ),
            statuses = listOf(),
            weeklyComparisonData = listOf(),
            weekDayFrequencyData = mapOf(),
            currentStreak = 12,
            bestStreak = 20,
            startedDaysAgo = 100
        ),
    )
}