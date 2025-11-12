package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
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
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitWithAnalytics
import com.shub39.grit.habits.presentation.HabitsPageAction
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarMap(
    canSeeContent: Boolean,
    onAction: (HabitsPageAction) -> Unit,
    calendarState: CalendarState,
    currentHabit: HabitWithAnalytics,
    primary: Color
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    AnalyticsCard(
        title = stringResource(R.string.monthly_progress),
        icon = Icons.Rounded.CalendarMonth,
        canSeeContent = canSeeContent,
        onPlusClick = { onAction(HabitsPageAction.OnShowPaywall) }
    ) {
        HorizontalCalendar(
            state = calendarState,
            modifier = Modifier
                .height(350.dp)
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            userScrollEnabled = canSeeContent,
            monthHeader = {
                Box(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = it.yearMonth.month.name + " ${it.yearMonth.year}",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            dayContent = { day ->
                if (day.position.name == "MonthDate") {
                    val done = currentHabit.statuses.any { it.date == day.date }
                    val validDate = day.date <= today && canSeeContent && day.date.dayOfWeek in currentHabit.habit.days

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(45.dp)
                            .clickable(enabled = validDate) {
                                onAction(
                                    HabitsPageAction.InsertStatus(
                                        currentHabit.habit,
                                        day.date
                                    )
                                )
                            }
                            .then(
                                if (done) {
                                    val donePrevious =
                                        currentHabit.statuses.any { it.date == day.date.minusDays(1) }
                                    val doneAfter =
                                        currentHabit.statuses.any { it.date == day.date.plusDays(1) }

                                    Modifier.background(
                                        color = primary.copy(alpha = 0.2f),
                                        shape = if (donePrevious && doneAfter) {
                                            RoundedCornerShape(5.dp)
                                        } else if (donePrevious) {
                                            RoundedCornerShape(
                                                topStart = 5.dp,
                                                bottomStart = 5.dp,
                                                topEnd = 20.dp,
                                                bottomEnd = 20.dp
                                            )
                                        } else if (doneAfter) {
                                            RoundedCornerShape(
                                                topStart = 20.dp,
                                                bottomStart = 20.dp,
                                                topEnd = 5.dp,
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
                            text = day.date.day.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                            color = if (done) primary
                            else if (!validDate) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    GritTheme {
        CalendarMap(
            canSeeContent = true,
            onAction = {},
            calendarState = rememberCalendarState(),
            currentHabit = HabitWithAnalytics(
                habit = Habit(
                    id = 0,
                    title = "Test Habit",
                    description = "A desc",
                    time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    days = DayOfWeek.entries.toSet(),
                    index = 1,
                    reminder = false
                ),
                statuses = emptyList(),
                weeklyComparisonData = emptyList(),
                weekDayFrequencyData = emptyMap(),
                currentStreak = 1,
                bestStreak = 1,
                startedDaysAgo = 1,
            ),
            primary = MaterialTheme.colorScheme.primary,
        )
    }
}