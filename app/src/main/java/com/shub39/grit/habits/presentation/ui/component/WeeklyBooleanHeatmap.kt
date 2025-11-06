package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitWithAnalytics
import com.shub39.grit.habits.presentation.HabitsPageAction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyBooleanHeatMap(
    heatMapState: HeatMapCalendarState,
    onAction: (HabitsPageAction) -> Unit,
    currentHabit: HabitWithAnalytics,
    primary: Color,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    AnalyticsCard(
        title = stringResource(R.string.weekly_progress),
        icon = Icons.Rounded.ViewWeek,
        modifier = modifier
    ) {
        HeatMapCalendar(
            state = heatMapState,
            modifier = Modifier.padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            monthHeader = {
                Box(
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = it.yearMonth.month.getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            weekHeader = {
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp, top = 2.dp, bottom = 2.dp)
                        .size(30.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = it.name.take(1),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            dayContent = { day, _ ->
                val done = currentHabit.statuses.any { it.date == day.date }
                val validDay = day.date <= today && day.date.dayOfWeek in currentHabit.habit.days

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(30.dp)
                        .clickable(enabled = validDay) {
                            onAction(
                                HabitsPageAction.InsertStatus(
                                    currentHabit.habit,
                                    day.date
                                )
                            )
                        }
                        .then(
                            if (done) {
                                val donePrevious = currentHabit.statuses.any { it.date == day.date.minusDays(1) }
                                val doneAfter = currentHabit.statuses.any { it.date == day.date.plusDays(1) }

                                Modifier.background(
                                    color = primary.copy(alpha = 0.2f),
                                    shape = if (donePrevious && doneAfter) {
                                        RoundedCornerShape(5.dp)
                                    } else if (donePrevious) {
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            bottomStart = 20.dp,
                                            topEnd = 5.dp,
                                            bottomEnd = 20.dp
                                        )
                                    } else if (doneAfter) {
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            bottomStart = 5.dp,
                                            topEnd = 20.dp,
                                            bottomEnd = 5.dp
                                        )
                                    } else {
                                        RoundedCornerShape(20.dp)
                                    }
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                        color = if (done) primary
                        else if (!validDay) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        WeeklyBooleanHeatMap(
            heatMapState = rememberHeatMapCalendarState(),
            onAction = {},
            currentHabit = HabitWithAnalytics(
                habit = Habit(
                    id = 0,
                    title = "Test Habit",
                    description = "A desc",
                    time = LocalDateTime.now(),
                    days = DayOfWeek.entries.toSet(),
                    index = 1,
                    reminder = true
                ),
                statuses = listOf(),
                weeklyComparisonData = listOf(),
                weekDayFrequencyData = mapOf(),
                currentStreak = 1,
                bestStreak = 1,
                startedDaysAgo = 1,
            ),
            primary = MaterialTheme.colorScheme.primary,
        )
    }
}