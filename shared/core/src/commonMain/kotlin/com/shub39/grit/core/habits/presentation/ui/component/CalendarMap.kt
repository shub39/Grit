/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.shub39.grit.core.habits.domain.StreakPosition
import com.shub39.grit.core.habits.domain.calendarMapStreakShape
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.shared_ui.endItemShape
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.utils.AllPreviews
import com.shub39.grit.core.utils.now
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.calendar_month
import grit.shared.core.generated.resources.monthly_progress
import kotlin.time.ExperimentalTime
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
import org.jetbrains.compose.resources.stringResource

/** Boolean Calendar map highlighting days */
@OptIn(ExperimentalTime::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarMap(
    canSeeContent: Boolean,
    onAction: (HabitsAction) -> Unit,
    calendarState: CalendarState,
    currentHabit: HabitWithAnalytics,
    primary: Color,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    val doneDates =
        remember(currentHabit.statuses) { currentHabit.statuses.map { it.date }.toSet() }
    val edgeWeeks =
        listOf(calendarState.firstDayOfWeek, daysStartingFrom(calendarState.firstDayOfWeek).last())

    AnalyticsCard(
        title = stringResource(Res.string.monthly_progress),
        icon = Res.drawable.calendar_month,
        shape = endItemShape(topRadius = 8, bottomRadius = 28),
        canSeeContent = canSeeContent,
        onPlusClick = onNavigateToPaywall,
        header = {
            CardArrows(
                onBackAction = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.minus(1, DateTimeUnit.MONTH)
                        )
                    }
                },
                onForwardAction = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.plus(1, DateTimeUnit.MONTH)
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) {
        HorizontalCalendar(
            state = calendarState,
            modifier = Modifier.animateContentSize().padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            userScrollEnabled = canSeeContent,
            monthHeader = {
                Box(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text =
                            it.yearMonth.format(
                                YearMonth.Format {
                                    monthName(MonthNames.ENGLISH_FULL)
                                    char(' ')
                                    year()
                                }
                            ),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
            dayContent = { day ->
                if (day.position.name == "MonthDate") {
                    val done = day.date in doneDates
                    val validDate =
                        day.date <= today && day.date.dayOfWeek in currentHabit.habit.days

                    Box(
                        modifier =
                            Modifier.padding(
                                    top = 1.dp,
                                    bottom = 1.dp,
                                    start =
                                        if (day.date.dayOfWeek == edgeWeeks.first()) 4.dp else 0.dp,
                                    end = if (day.date.dayOfWeek == edgeWeeks.last()) 4.dp else 0.dp,
                                )
                                .fillMaxWidth()
                                .height(40.dp)
                                .clickable(enabled = validDate) {
                                    onAction(
                                        HabitsAction.InsertStatus(currentHabit.habit, day.date)
                                    )
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (done) {
                            val donePrevious = day.date.minusDays(1) in doneDates
                            val doneAfter = day.date.plusDays(1) in doneDates
                            val streakPosition =
                                when {
                                    donePrevious && doneAfter -> StreakPosition.MIDDLE
                                    donePrevious -> StreakPosition.END
                                    doneAfter -> StreakPosition.START
                                    else -> StreakPosition.ISOLATED
                                }

                            Box(
                                modifier =
                                    Modifier.fillMaxSize()
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape =
                                                calendarMapStreakShape(
                                                    streakPosition = streakPosition,
                                                    isFirstDayOfWeek =
                                                        day.date.dayOfWeek == edgeWeeks.first(),
                                                    isLastDayOfWeek =
                                                        day.date.dayOfWeek == edgeWeeks.last(),
                                                    isFirstDayOfMonth = day.date.day == 1,
                                                    isLastDayOfMonth = day.date.plusDays(1).day == 1,
                                                ),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                val isStreakEnd =
                                    streakPosition == StreakPosition.START ||
                                        streakPosition == StreakPosition.END

                                if (isStreakEnd) {
                                    Box(
                                        modifier =
                                            Modifier.fillMaxSize()
                                                .padding(2.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    shape = CircleShape,
                                                ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = day.date.day.toString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                } else {
                                    Text(
                                        text = day.date.day.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = day.date.day.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color =
                                    if (!validDate)
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
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
            calendarState =
                rememberCalendarState(
                    startMonth = YearMonth.now().minusYears(1),
                    endMonth = YearMonth.now(),
                    firstVisibleMonth = YearMonth.now(),
                ),
            currentHabit =
                HabitWithAnalytics(
                    habit =
                        Habit(
                            id = 1,
                            title = "Test Habit",
                            description = "Just a test Habit",
                            time = LocalDateTime.now(),
                            days = DayOfWeek.entries.toSet(),
                            index = 1,
                            reminder = false,
                        ),
                    statuses =
                        (0..40).map {
                            HabitStatus(
                                habitId = 1,
                                date = LocalDate.now().minus(it, DateTimeUnit.DAY),
                            )
                        },
                    weeklyComparisonData = listOf(),
                    weekDayFrequencyData = mapOf(),
                    currentStreak = 0,
                    bestStreak = 0,
                    startedDaysAgo = 0,
                ),
            primary = MaterialTheme.colorScheme.primary,
            onNavigateToPaywall = {},
        )
    }
}
