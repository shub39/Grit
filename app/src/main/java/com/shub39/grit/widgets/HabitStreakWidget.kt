package com.shub39.grit.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.data.toHabit
import com.shub39.grit.core.data.toHabitStatus
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.habits.data.database.HabitDao
import com.shub39.grit.habits.data.database.HabitStatusDao
import com.shub39.grit.habits.data.repository.countCurrentStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HabitStreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HabitStreakWidget()
}

private val directionKey = ActionParameters.Key<String>("DirectionKey")
private val habitIdKey = longPreferencesKey("habit_id")

class HabitStreakWidgetRepository(
    private val context: Context,
    private val statusDao: HabitStatusDao,
    private val habitDao: HabitDao
) {
    suspend fun update(
        glanceId: GlanceId,
        actionParameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val habitIds = habitDao.getAllHabits().map { it.id }
            val currentId = prefs[habitIdKey] ?: habitIds.firstOrNull()
            val index = habitIds.indexOf(currentId)

            val newIndex = when (actionParameters[directionKey]) {
                "back" -> (index - 1).coerceAtLeast(0)
                else -> (index + 1).coerceAtMost(habitIds.size - 1)
            }
            
            if (habitIds.isNotEmpty()) {
                prefs[habitIdKey] = habitIds[newIndex]
            }
        }

        HabitStreakWidget().update(context, glanceId)
    }

    fun getHabits(): Flow<List<Habit>> {
        return habitDao
            .getAllHabitsFlow()
            .map { flow -> flow.map { it.toHabit() } }
            .distinctUntilChanged()
    }

    fun getHabitStatuses(): Flow<List<HabitStatus>> {
        return statusDao
            .getAllHabitStatuses()
            .map { flow ->
                flow.map { it.toHabitStatus() }
            }
            .distinctUntilChanged()
    }

    suspend fun update() {
        HabitStreakWidget().updateAll(context)
    }
}

class HabitStreakWidget : GlanceAppWidget(), KoinComponent {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<HabitStreakWidgetRepository>()

        provideContent {
            val coroutineScope = rememberCoroutineScope()
            val state = currentState<Preferences>()

            val allHabits by repo.getHabits().collectAsState(emptyList())
            val allStatuses by repo.getHabitStatuses().collectAsState(emptyList())
            val habitId = state[habitIdKey] ?: allHabits.firstOrNull()?.id

            val habit = allHabits.find { it.id == habitId } ?: allHabits.firstOrNull()
            val statuses = allStatuses.filter { it.habitId == habitId }.map { it.date }

            GlanceTheme {
                HabitStreak(
                    context = context,
                    habit = habit,
                    statuses = statuses,
                    onAction = { coroutineScope.launch { repo.update(id, it) } }
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Composable
    fun HabitStreak(
        context: Context,
        habit: Habit?,
        statuses: List<LocalDate>,
        onAction: (ActionParameters) -> Unit
    ) {
        if (habit != null) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val last5Days = (0..4).map { today.minus(it, DateTimeUnit.DAY) }.reversed()
            val currentStreak = countCurrentStreak(statuses)

            Scaffold(
                titleBar = {
                    Row(
                        modifier = GlanceModifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.title,
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Row {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.round_arrow_back_ios_24),
                                onClick = {
                                    onAction(
                                        actionParametersOf(directionKey to "back")
                                    )
                                },
                                backgroundColor = null,
                                contentColor = GlanceTheme.colors.onPrimaryContainer,
                                contentDescription = "Replay",
                                modifier = GlanceModifier.size(36.dp)
                            )

                            Spacer(modifier = GlanceModifier.size(8.dp))

                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.round_arrow_forward_ios_24),
                                onClick = {
                                    onAction(
                                        actionParametersOf(directionKey to "next")
                                    )
                                },
                                contentColor = GlanceTheme.colors.onPrimaryContainer,
                                backgroundColor = null,
                                contentDescription = "Right Arrow",
                                modifier = GlanceModifier.size(36.dp)
                            )
                        }
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    Column {
                        Text(
                            text = "\uD83D\uDD25 $currentStreak",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        )

                        Spacer(GlanceModifier.height(16.dp))

                        Row {
                            last5Days.forEach {
                                Box(
                                    modifier = GlanceModifier
                                        .background(
                                            if (it in statuses) {
                                                GlanceTheme.colors.primary
                                            } else {
                                                GlanceTheme.colors.background
                                            }
                                        )
                                        .cornerRadius(10.dp)
                                        .size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.dayOfWeek.name.take(1),
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (it in statuses) {
                                                GlanceTheme.colors.background
                                            } else {
                                                GlanceTheme.colors.primary
                                            }
                                        )
                                    )
                                }

                                Spacer(GlanceModifier.width(8.dp))
                            }
                        }
                    }

                    Spacer(GlanceModifier.width(48.dp))

                    Text(
                        text = when (currentStreak) {
                            in 0..10 -> "\uD83C\uDFC1"
                            in 10..50 -> "\uD83D\uDEB4"
                            in 50..100 -> "\uD83D\uDE99"
                            in 100..200 -> "\uD83D\uDE9D"
                            in 200..400 -> "\uD83C\uDFCE\uFE0F"
                            else -> "\uD83D\uDE80"
                        },
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 60.sp
                        )
                    )
                }
            }
        } else {
            Scaffold {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = context.getString(R.string.add),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
