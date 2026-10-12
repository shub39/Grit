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
package com.shub39.grit.shared.ui.habit.ui.component

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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.habits.StreakPosition
import com.shub39.grit.shared.ui.calendarMapStreakShape
import com.shub39.grit.shared.ui.theme.flexFontRounded
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

@Composable
fun CalendarMonthHeader(
    calendarMonth: CalendarMonth,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    padding: PaddingValues = PaddingValues(start = 4.dp, end = 4.dp, top = 32.dp, bottom = 8.dp),
) {
    Box(modifier = modifier.padding(padding)) {
        Text(
            text =
                calendarMonth.yearMonth.format(
                    YearMonth.Format {
                        monthName(MonthNames.ENGLISH_FULL)
                        char(' ')
                        year()
                    }
                ),
            style = style.copy(color = colorScheme.secondary, fontFamily = flexFontRounded()),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun YearlyCalendarDayContent(
    day: CalendarDay,
    isMeasurable: Boolean,
    targetValue: Double,
    doneDates: List<HabitStatus>,
    today: LocalDate,
    habitDays: Set<DayOfWeek>,
    edgeWeeks: List<DayOfWeek>,
    modifier: Modifier = Modifier,
    style: TextStyle =
        MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = flexFontRounded()),
    onDateClick: (LocalDate, Double) -> Unit,
) {
    if (day.position != DayPosition.MonthDate) return

    val done = doneDates.find { day.date == it.date }
    val validDate = day.date <= today && day.date.dayOfWeek in habitDays

    val donePrevious = doneDates.any { it.date == day.date.minusDays(1) }
    val doneAfter = doneDates.any { it.date == day.date.plusDays(1) }
    val streakPosition: StreakPosition =
        when {
            donePrevious && doneAfter -> MIDDLE
            donePrevious -> END
            doneAfter -> START
            else -> ISOLATED
        }

    val backgroundColor =
        if (done != null) {
            colorScheme.primary.copy(
                alpha =
                    if (isMeasurable) {
                        (done.value / targetValue).toFloat().coerceIn(0f, 1f)
                    } else 1f
            )
        } else colorScheme.surface
    val contentColor =
        if (done != null) {
            if (!isMeasurable) colorScheme.onPrimary
            else {
                val progress = (done.value / targetValue).toFloat().coerceIn(0f, 1f)
                if (progress >= 0.7f) colorScheme.onPrimary else colorScheme.onSurface
            }
        } else colorScheme.onSurface.copy(alpha = if (!validDate) 0.5f else 1f)

    Box(
        modifier =
            modifier
                .padding(top = 1.dp, bottom = 1.dp)
                .fillMaxWidth()
                .height(20.dp)
                .clip(
                    calendarMapStreakShape(
                        streakPosition = streakPosition,
                        isFirstDayOfWeek = day.date.dayOfWeek == edgeWeeks.first(),
                        isLastDayOfWeek = day.date.dayOfWeek == edgeWeeks.last(),
                        isFirstDayOfMonth = day.date.day == 1,
                        isLastDayOfMonth = day.date.plusDays(1).day == 1,
                    )
                )
                .clickable(enabled = validDate) { onDateClick(day.date, done?.value ?: 0.0) },
        contentAlignment = Alignment.Center,
    ) {
        if (done != null) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = day.date.day.toString(), style = style, color = contentColor)
            }
        } else if (validDate) {
            Box(
                modifier =
                    Modifier.matchParentSize()
                        .background(color = backgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = day.date.day.toString(), style = style, color = contentColor)
            }
        }
    }
}

@Composable
fun MonthlyCalendarDayContent(
    day: CalendarDay,
    isMeasurable: Boolean,
    targetValue: Double,
    doneDates: List<HabitStatus>,
    today: LocalDate,
    habitDays: Set<DayOfWeek>,
    edgeWeeks: List<DayOfWeek>,
    onDateClick: (LocalDate, Double) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = flexFontRounded()),
) {
    if (day.position != DayPosition.MonthDate) return

    val done = doneDates.find { day.date == it.date }
    val validDate = day.date <= today && day.date.dayOfWeek in habitDays

    val donePrevious = doneDates.any { it.date == day.date.minusDays(1) }
    val doneAfter = doneDates.any { it.date == day.date.plusDays(1) }
    val streakPosition: StreakPosition =
        when {
            donePrevious && doneAfter -> MIDDLE
            donePrevious -> END
            doneAfter -> START
            else -> ISOLATED
        }

    val backgroundColor =
        if (done != null) {
            colorScheme.primary.copy(
                alpha =
                    if (isMeasurable) {
                        (done.value / targetValue).toFloat().coerceIn(0f, 1f)
                    } else 1f
            )
        } else colorScheme.surface
    val contentColor =
        if (done != null) {
            if (!isMeasurable) colorScheme.onPrimary
            else {
                val progress = (done.value / targetValue).toFloat().coerceIn(0f, 1f)
                if (progress >= 0.7f) colorScheme.onPrimary else colorScheme.onSurface
            }
        } else colorScheme.onSurface.copy(alpha = if (!validDate) 0.5f else 1f)

    Box(
        modifier =
            modifier
                .padding(
                    top = 1.dp,
                    bottom = 1.dp,
                    start = if (day.date.dayOfWeek == edgeWeeks.first()) 4.dp else 0.dp,
                    end = if (day.date.dayOfWeek == edgeWeeks.last()) 4.dp else 0.dp,
                )
                .fillMaxWidth()
                .height(height)
                .clip(
                    calendarMapStreakShape(
                        streakPosition = streakPosition,
                        isFirstDayOfWeek = day.date.dayOfWeek == edgeWeeks.first(),
                        isLastDayOfWeek = day.date.dayOfWeek == edgeWeeks.last(),
                        isFirstDayOfMonth = day.date.day == 1,
                        isLastDayOfMonth = day.date.plusDays(1).day == 1,
                    )
                )
                .clickable(enabled = validDate) { onDateClick(day.date, done?.value ?: 0.0) },
        contentAlignment = Alignment.Center,
    ) {
        if (done != null) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = day.date.day.toString(), style = style, color = contentColor)
            }
        } else {
            Text(text = day.date.day.toString(), style = style, color = contentColor)
        }
    }
}
