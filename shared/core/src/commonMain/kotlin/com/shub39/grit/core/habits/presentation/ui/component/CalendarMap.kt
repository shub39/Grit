package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.utils.AllPreviews
import com.shub39.grit.core.utils.now
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.calendar_month
import grit.shared.core.generated.resources.monthly_progress
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

/**
 * Boolean Calendar map highlighting days
 */
@OptIn(ExperimentalTime::class)
@Composable
fun CalendarMap(
    canSeeContent: Boolean,
    onAction: (HabitsAction) -> Unit,
    calendarState: CalendarState,
    currentHabit: HabitWithAnalytics,
    primary: Color,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    AnalyticsCard(
        title = stringResource(Res.string.monthly_progress),
        icon = Res.drawable.calendar_month,
        canSeeContent = canSeeContent,
        onPlusClick = onNavigateToPaywall,
        header = {
            Row {
                IconButton(
                    onClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth
                                    .minus(1, DateTimeUnit.MONTH)
                            )
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = null,
                        tint = primary
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth
                                    .plus(1, DateTimeUnit.MONTH)
                            )
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward),
                        contentDescription = null,
                        tint = primary
                    )
                }
            }
        },
        modifier = modifier
    ) {
        HorizontalCalendar(
            state = calendarState,
            modifier = Modifier
                .animateContentSize()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            userScrollEnabled = canSeeContent,
            monthHeader = {
                Box(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = it.yearMonth.format(
                            YearMonth.Format {
                                monthName(MonthNames.ENGLISH_FULL)
                                char(' ')
                                year()
                            }
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            dayContent = { day ->
                if (day.position.name == "MonthDate") {
                    val done = currentHabit.statuses.any { it.date == day.date }
                    val validDate =
                        day.date <= today && canSeeContent && day.date.dayOfWeek in currentHabit.habit.days

                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .clickable(enabled = validDate) {
                                onAction(
                                    HabitsAction.InsertStatus(
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
                                        shape = when {
                                            donePrevious && doneAfter -> RoundedCornerShape(4.dp)
                                            donePrevious -> RoundedCornerShape(
                                                topEnd = 1000.dp,
                                                bottomEnd = 1000.dp,
                                                topStart = 4.dp,
                                                bottomStart = 4.dp
                                            )
                                            doneAfter -> RoundedCornerShape(
                                                topStart = 1000.dp,
                                                bottomStart = 1000.dp,
                                                topEnd = 4.dp,
                                                bottomEnd = 4.dp
                                            )
                                            else -> CircleShape
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

@AllPreviews
@Composable
private fun Preview() {
    GritTheme {
        CalendarMap(
            canSeeContent = true,
            onAction = {},
            calendarState = rememberCalendarState(
                startMonth = YearMonth.now().minusYears(1),
                endMonth = YearMonth.now(),
                firstVisibleMonth = YearMonth.now()
            ),
            currentHabit = HabitWithAnalytics(
                habit = Habit(
                    id = 1,
                    title = "Test Habit",
                    description = "Just a test Habit",
                    time = LocalDateTime.now(),
                    days = DayOfWeek.entries.toSet(),
                    index = 1,
                    reminder = false
                ),
                statuses = (0..40).map {
                    HabitStatus(
                        habitId = 1,
                        date = LocalDate.now().minus(it, DateTimeUnit.DAY)
                    )
                },
                weeklyComparisonData = listOf(),
                weekDayFrequencyData = mapOf(),
                currentStreak = 0,
                bestStreak = 0,
                startedDaysAgo = 0
            ),
            primary = MaterialTheme.colorScheme.primary,
            onNavigateToPaywall = { },
        )
    }
}