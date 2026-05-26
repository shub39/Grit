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
package com.shub39.grit.core.habits.presentation.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.LocalWindowSizeClass
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.StreakPosition
import com.shub39.grit.core.habits.domain.calendarMapStreakShape
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.theme.flexFontEmphasis
import grit.shared.generated.resources.Res
import grit.shared.generated.resources.arrow_back
import grit.shared.generated.resources.calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Calendar(
    state: HabitState,
    onDateClick: (Habit, LocalDate) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentHabit =
        state.habitsWithAnalytics.find { it.habit.id == state.analyticsHabitId } ?: return

    val windowSizeClass = LocalWindowSizeClass.current

    val today = LocalDate.now()

    val doneDates =
        remember(currentHabit.statuses) { currentHabit.statuses.map { it.date }.toSet() }

    val state =
        rememberCalendarState(
            startMonth = YearMonth(year = 2024, month = Month.JANUARY),
            endMonth = YearMonth.now(),
            firstVisibleMonth = YearMonth.now(),
            firstDayOfWeek = state.startingDay,
        )
    val edgeWeeks = listOf(state.firstDayOfWeek, daysStartingFrom(state.firstDayOfWeek).last())

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = stringResource(Res.string.calendar), fontFamily = flexFontEmphasis())
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = "Navigate Back",
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
            windowInsets =
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    WindowInsets(0)
                } else {
                    TopAppBarDefaults.windowInsets
                },
        )

        VerticalCalendar(
            modifier = Modifier.padding(horizontal = 16.dp),
            state = state,
            reverseLayout = true,
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
                                .clickable(enabled = validDate) {
                                    onDateClick(currentHabit.habit, day.date)
                                },
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
