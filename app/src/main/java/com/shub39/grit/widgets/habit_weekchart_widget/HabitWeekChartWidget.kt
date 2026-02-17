package com.shub39.grit.widgets.habit_weekchart_widget

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
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
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
import kotlin.math.roundToInt
import kotlin.random.Random

class HabitWeekChartWidget : GlanceAppWidget(), KoinComponent {

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
                                this@HabitWeekChartWidget.update(context, id)
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
                    weeklyComparisonData = (0..8).map { it.toDouble() },
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
                modifier = GlanceModifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Row {
                    Text(
                        text = "${habitWithAnalytics.weeklyComparisonData.average().roundToInt()} ",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = if (size.width >= WidgetSize.Width4) {
                            "times per week (avg)"
                        } else "(avg)",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }

                Row(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    val data = habitWithAnalytics.weeklyComparisonData.takeLast(
                        when {
                            size.width >= WidgetSize.Width4 -> 9
                            else -> 6
                        }
                    )
                    val maxData = data.maxOrNull() ?: 0.0

                    data.forEach { double ->
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = GlanceModifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = GlanceModifier.padding(end =  4.dp)
                            ) {
                                Box(
                                    modifier = GlanceModifier
                                        .width(20.dp)
                                        .height((90 * (double.roundToInt() / maxData)).dp)
                                        .then(
                                            if (roundedCornerSupported) {
                                                GlanceModifier
                                                    .background(GlanceTheme.colors.primary)
                                                    .cornerRadius(16.dp)
                                            } else {
                                                GlanceModifier.background(
                                                    ImageProvider(R.drawable.rounded_16dp),
                                                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = "${double.roundToInt()}",
                                        style = TextStyle(
                                            fontSize = 10.sp,
                                            color = GlanceTheme.colors.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
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
            weeklyComparisonData = (0..10).map { Random.nextDouble(0.0, 10.0) },
            weekDayFrequencyData = mapOf(),
            currentStreak = 12,
            bestStreak = 20,
            startedDaysAgo = 100
        ),
    )
}