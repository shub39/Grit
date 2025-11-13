package com.shub39.grit.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.kizitonwose.calendar.core.now
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.data.toHabit
import com.shub39.grit.core.data.toHabitStatusEntity
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.habits.data.database.HabitDao
import com.shub39.grit.habits.data.database.HabitStatusDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.ExperimentalTime

class HabitOverviewWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HabitOverviewWidget()
}

@OptIn(ExperimentalTime::class)
class HabitOverviewWidgetRepository(
    private val context: Context,
    private val statusDao: HabitStatusDao,
    private val habitDao: HabitDao
) {
    suspend fun update() {
        HabitOverviewWidget().updateAll(context)
    }

    suspend fun setStatus(id: Long) {
        statusDao.insertHabitStatus(
            habitStatusEntity = HabitStatus(
                habitId = id,
                date = LocalDate.now()
            ).toHabitStatusEntity()
        )
    }

    suspend fun deleteStatus(id: Long) {
        statusDao.deleteStatus(
            habitId = id,
            date = LocalDate.now()
        )
    }

    fun getHabits(): Flow<List<Habit>> {
        return habitDao
            .getAllHabitsFlow()
            .map { flow ->
                flow.map { it.toHabit() }
            }
            .distinctUntilChanged()
    }

    fun getHabitStatuses(): Flow<List<Long>> {
        return statusDao
            .getAllHabitStatuses()
            .map { flow ->
                flow.filter { it.date == LocalDate.now() }.map { it.habitId }
            }
            .distinctUntilChanged()
    }
}

@OptIn(ExperimentalTime::class)
class HabitOverviewWidget : GlanceAppWidget(), KoinComponent {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<HabitOverviewWidgetRepository>()

        provideContent {
            val coroutineScope = rememberCoroutineScope()
            val habits by repo.getHabits().collectAsState(initial = emptyList())
            val completedHabitIds by repo.getHabitStatuses().collectAsState(initial = emptyList())

            GlanceTheme {
                HabitOverview(
                    context = context,
                    noHabits = habits.isEmpty(),
                    habits = habits.filter { LocalDate.now().dayOfWeek in it.days },
                    completedHabitIds = completedHabitIds,
                    onHabitClick = {
                        coroutineScope.launch {
                            if (it in completedHabitIds) {
                                repo.deleteStatus(it)
                            } else {
                                repo.setStatus(it)
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun HabitOverview(
        context: Context,
        noHabits: Boolean,
        habits: List<Habit>,
        completedHabitIds: List<Long>,
        onHabitClick: (Long) -> Unit
    ) {
        Scaffold(
            modifier = GlanceModifier.fillMaxSize(),
            backgroundColor = GlanceTheme.colors.widgetBackground,
            titleBar = {
                Row(
                    modifier = GlanceModifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = context.getString(R.string.habits),
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                    )

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    Text(
                        text = "${completedHabitIds.size}/${habits.size}",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        ) {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                items(habits, itemId = { it.id }) { habit ->
                    val done = habit.id in completedHabitIds

                    Column {
                        Column(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .background(
                                    if (done) GlanceTheme.colors.primary
                                    else GlanceTheme.colors.secondaryContainer
                                )
                                .cornerRadius(if (done) 20.dp else 100.dp)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .clickable { onHabitClick(habit.id) }
                        ) {
                            Column(modifier = GlanceModifier.padding(8.dp)) {
                                Text(
                                    text = habit.title,
                                    modifier = GlanceModifier
                                        .fillMaxWidth(),
                                    style = TextStyle(
                                        color = if (done) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSecondaryContainer,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )

                                if (habit.description.isNotEmpty()) {
                                    Text(
                                        text = habit.description,
                                        modifier = GlanceModifier
                                            .fillMaxWidth(),
                                        style = TextStyle(
                                            color = if (done) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSecondaryContainer,
                                            fontSize = 12.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Spacer(GlanceModifier.height(4.dp))
                    }
                }

                item {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (noHabits) {
                                context.getString(R.string.add)
                            } else if (habits.isEmpty()) {
                                context.getString(R.string.no_habit_for_today)
                            } else "",
                            modifier = GlanceModifier
                                .clickable(actionStartActivity<MainActivity>())
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                item {
                    Spacer(GlanceModifier.height(12.dp))
                }
            }
        }
    }
}