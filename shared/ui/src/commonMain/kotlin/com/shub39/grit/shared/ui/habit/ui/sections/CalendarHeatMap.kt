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
package com.shub39.grit.shared.ui.habit.ui.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.VerticalYearCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.yearcalendar.rememberYearCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.Year
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.CalendarType
import com.shub39.grit.core.toFormattedString
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.components.GritBottomSheet
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.ui.component.CalendarMonthHeader
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import com.shub39.grit.shared.ui.toStringRes
import grit.shared.ui.generated.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CalendarHeatMap(
    state: HabitState,
    onNavigateBack: () -> Unit,
    onChangeSelectedDay: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var calendarType by rememberSaveable { mutableStateOf(CalendarType.MONTH) }
    val windowSizeClass = LocalWindowSizeClass.current
    val today = LocalDate.now()
    val totalHabits = state.habitsWithAnalytics.size
    var selectedDay: LocalDate? by remember { mutableStateOf(null) }

    LaunchedEffect(selectedDay) { onChangeSelectedDay(selectedDay) }

    if (state.overallAnalytics.completedHabits != null) {
        GritBottomSheet(onDismissRequest = { selectedDay = null }, padding = 16.dp) {
            state.overallAnalytics.completedHabits?.let { (date, habits) ->
                Text(
                    text = date.toFormattedString(),
                    fontFamily = flexFontRounded(),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (habits.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        habits.forEachIndexed { index, habit ->
                            val shape =
                                when {
                                    habits.size == 1 -> RoundedCornerShape(12.dp)
                                    index == 0 -> leadingItemShape(topRadius = 12)
                                    index == habits.size - 1 -> endItemShape(bottomRadius = 12)
                                    else -> RoundedCornerShape(4.dp)
                                }

                            ListItem(
                                colors =
                                    ListItemDefaults.colors(
                                        containerColor =
                                            MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor =
                                            MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                modifier = Modifier.padding(vertical = 1.dp).clip(shape),
                                headlineContent = { Text(habit) },
                            )
                        }
                    }
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = stringResource(Res.string.calendar), fontFamily = flexFontEmphasis())
            },
            navigationIcon = {
                FilledTonalIconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.nav_arrow_back),
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

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            CalendarType.entries.forEach { entry ->
                ToggleButton(
                    checked = entry == calendarType,
                    onCheckedChange = { calendarType = entry },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(entry.toStringRes()))
                }
            }
        }

        AnimatedContent(targetState = calendarType, modifier = Modifier.fillMaxSize()) { type ->
            when (type) {
                YEAR -> {
                    YearlyMap(
                        today = today,
                        state = state,
                        totalHabits = totalHabits,
                        selectedDay = selectedDay,
                        onChangeSelectedDay = { selectedDay = it },
                    )
                }

                MONTH -> {
                    MonthlyMap(
                        state = state,
                        today = today,
                        totalHabits = totalHabits,
                        selectedDay = selectedDay,
                        onChangeSelectedDay = { selectedDay = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun YearlyMap(
    modifier: Modifier = Modifier,
    today: LocalDate,
    state: HabitState,
    totalHabits: Int,
    selectedDay: LocalDate?,
    onChangeSelectedDay: (LocalDate) -> Unit,
) {
    val calendarState =
        rememberYearCalendarState(
            startYear = Year(2024),
            endYear = Year(today.yearMonth.year),
            firstVisibleYear = Year(today.yearMonth.year),
            firstDayOfWeek = state.startingDay,
        )

    VerticalYearCalendar(
        modifier =
            modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
        state = calendarState,
        monthVerticalSpacing = 4.dp,
        monthHorizontalSpacing = 4.dp,
        reverseLayout = true,
        calendarScrollPaged = false,
        yearHeader = { calendarYear ->
            Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 48.dp, bottom = 0.dp)) {
                Text(
                    text = calendarYear.year.value.toString(),
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = flexFontRounded(),
                        ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
        monthHeader = { calendarMonth ->
            if (calendarMonth.yearMonth <= today.yearMonth) {
                CalendarMonthHeader(
                    calendarMonth = calendarMonth,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        dayContent = { day ->
            if (day.date > today || day.position != DayPosition.MonthDate)
                return@VerticalYearCalendar
            val count = state.overallAnalytics.heatMapData[day.date]

            val corners by
                animateDpAsState(targetValue = if (selectedDay == day.date) 1000.dp else 2.dp)

            Box(
                modifier =
                    Modifier.padding(1.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            shape = RoundedCornerShape(corners),
                            color =
                                when (count) {
                                    0 -> MaterialTheme.colorScheme.surfaceContainerHighest

                                    null -> MaterialTheme.colorScheme.surfaceContainerLowest

                                    else ->
                                        MaterialTheme.colorScheme.primary.copy(
                                            alpha = (count.toFloat() / totalHabits).coerceIn(0f, 1f)
                                        )
                                },
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = count != null) { onChangeSelectedDay(day.date) }
            )
        },
    )
}

@Composable
private fun MonthlyMap(
    modifier: Modifier = Modifier,
    state: HabitState,
    today: LocalDate,
    totalHabits: Int,
    selectedDay: LocalDate?,
    onChangeSelectedDay: (LocalDate) -> Unit,
) {
    val calendarState =
        rememberCalendarState(
            startMonth = YearMonth(year = 2024, month = Month.JANUARY),
            endMonth = YearMonth.now(),
            firstVisibleMonth = YearMonth.now(),
            firstDayOfWeek = state.startingDay,
        )

    VerticalCalendar(
        modifier =
            modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        state = calendarState,
        monthHeader = { calendarMonth -> CalendarMonthHeader(calendarMonth = calendarMonth) },
        contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
        reverseLayout = true,
        dayContent = { day ->
            if (day.date > today || day.position != DayPosition.MonthDate) return@VerticalCalendar
            val count = state.overallAnalytics.heatMapData[day.date]

            val corners by
                animateDpAsState(targetValue = if (selectedDay == day.date) 1000.dp else 8.dp)

            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(1.dp)
                        .background(
                            shape = RoundedCornerShape(corners),
                            color =
                                when (count) {
                                    0 -> MaterialTheme.colorScheme.surfaceContainerHighest

                                    null -> MaterialTheme.colorScheme.surfaceContainerLowest

                                    else ->
                                        MaterialTheme.colorScheme.primary.copy(
                                            alpha = (count.toFloat() / totalHabits).coerceIn(0f, 1f)
                                        )
                                },
                        )
                        .clip(RoundedCornerShape(corners))
                        .clickable(enabled = count != null) { onChangeSelectedDay(day.date) },
                contentAlignment = Alignment.Center,
            ) {
                val textColor =
                    when (count) {
                        0 -> MaterialTheme.colorScheme.onSurface
                        null -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> {
                            val alpha = (count.toFloat() / totalHabits).coerceIn(0f, 1f)

                            if (alpha in 0f..0.5f) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
                        }
                    }

                Text(
                    text = day.date.day.toString(),
                    color = textColor,
                    fontFamily = flexFontRounded(),
                )
            }
        },
    )
}
