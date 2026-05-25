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
package com.shub39.grit.core.habits.presentation.ui.component.stats

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.GritPreviewWrapper
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.StreakPosition
import com.shub39.grit.core.habits.domain.calendarMapStreakShape
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.habits.presentation.ui.component.AnalyticsCard
import com.shub39.grit.core.habits.presentation.ui.component.CardArrows
import com.shub39.grit.core.now
import grit.shared.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

/**
 * Boolean Calendar highlighting completed days and streaks
 *
 * @param canSeeContent is user subbed?
 * @param calendarState calendar state object
 * @param statuses list of [HabitStatus]
 * @param days set of [DayOfWeek]
 */
@Composable
fun CalendarMap(
    canSeeContent: Boolean,
    calendarState: CalendarState,
    statuses: List<HabitStatus>,
    days: Set<DayOfWeek>,
    onNavigateToPaywall: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    val doneDates = remember(statuses) { statuses.map { it.date }.toSet() }
    val edgeWeeks =
        listOf(calendarState.firstDayOfWeek, daysStartingFrom(calendarState.firstDayOfWeek).last())

    AnalyticsCard(
        title = stringResource(Res.string.monthly_progress),
        icon = Res.drawable.calendar_month,
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
            modifier =
                Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .animateContentSize()
                    .padding(vertical = 16.dp),
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
                    val validDate = day.date <= today && day.date.dayOfWeek in days

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
                            Modifier.padding(
                                    top = 1.dp,
                                    bottom = 1.dp,
                                    start =
                                        if (day.date.dayOfWeek == edgeWeeks.first()) 4.dp else 0.dp,
                                    end = if (day.date.dayOfWeek == edgeWeeks.last()) 4.dp else 0.dp,
                                )
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(
                                    calendarMapStreakShape(
                                        streakPosition = streakPosition,
                                        isFirstDayOfWeek = day.date.dayOfWeek == edgeWeeks.first(),
                                        isLastDayOfWeek = day.date.dayOfWeek == edgeWeeks.last(),
                                        isFirstDayOfMonth = day.date.day == 1,
                                        isLastDayOfMonth = day.date.plusDays(1).day == 1,
                                    )
                                )
                                .clickable(enabled = validDate) { onDateClick(day.date) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (done) {
                            Box(
                                modifier =
                                    Modifier.fillMaxSize()
                                        .background(color = MaterialTheme.colorScheme.primary),
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

@PreviewWrapper(GritPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    CalendarMap(
        canSeeContent = true,
        calendarState =
            rememberCalendarState(
                startMonth = YearMonth.now().minusYears(1),
                endMonth = YearMonth.now(),
                firstVisibleMonth = YearMonth.now(),
            ),
        statuses =
            (0..40).map {
                HabitStatus(habitId = 1, date = LocalDate.now().minus(it, DateTimeUnit.DAY))
            },
        days = DayOfWeek.entries.toSet(),
        onNavigateToPaywall = {},
        onDateClick = {},
    )
}
