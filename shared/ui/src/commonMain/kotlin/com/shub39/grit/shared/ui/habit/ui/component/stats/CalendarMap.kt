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
package com.shub39.grit.shared.ui.habit.ui.component.stats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.habit.daysStartingFrom
import com.shub39.grit.shared.ui.habit.ui.component.AnalyticsCard
import com.shub39.grit.shared.ui.habit.ui.component.CalendarMonthHeader
import com.shub39.grit.shared.ui.habit.ui.component.CardArrows
import com.shub39.grit.shared.ui.habit.ui.component.MonthlyCalendarDayContent
import grit.shared.ui.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

/**
 * Boolean Calendar highlighting completed days and streaks
 *
 * @param canSeeContent is user subbed?
 * @param calendarState calendar state object
 * @param statuses list of [com.shub39.grit.core.habits.HabitStatus]
 * @param days set of [DayOfWeek]
 */
@Composable
fun CalendarMap(
    canSeeContent: Boolean,
    calendarState: CalendarState,
    statuses: List<HabitStatus>,
    days: Set<DayOfWeek>,
    startDate: LocalDate,
    onNavigateToPaywall: () -> Unit,
    onNavigateToCalendar: () -> Unit,
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
                onExpandAction = onNavigateToCalendar,
                enabled = canSeeContent,
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
                CalendarMonthHeader(
                    calendarMonth = it,
                    style = MaterialTheme.typography.titleMedium,
                    padding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 8.dp),
                )
            },
            dayContent = { day ->
                MonthlyCalendarDayContent(
                    day = day,
                    doneDates = doneDates,
                    today = today,
                    habitDays = days,
                    startDate = startDate,
                    edgeWeeks = edgeWeeks,
                    onDateClick = onDateClick,
                )
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
        startDate = LocalDate.now().minus(40, DateTimeUnit.DAY),
        onNavigateToPaywall = {},
        onDateClick = {},
        onNavigateToCalendar = {},
    )
}
