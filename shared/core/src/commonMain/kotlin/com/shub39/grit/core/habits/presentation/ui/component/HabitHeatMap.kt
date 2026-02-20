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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import com.shub39.grit.core.utils.AllPreviews
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.habit_map
import grit.shared.core.generated.resources.map
import kotlin.random.Random
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** Habit heat map, Github contributions style map showing no of habits completed in a day */
@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    totalHabits: Int,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    AnalyticsCard(
        title = stringResource(Res.string.habit_map),
        icon = Res.drawable.map,
        modifier = modifier,
        header = {
            Row {
                IconButton(onClick = { scope.launch { heatMapState.animateScrollBy(-100f) } }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = { scope.launch { heatMapState.animateScrollBy(100f) } }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_forward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                daysStartingFrom(heatMapState.firstDayOfWeek).forEach { dayOfWeek ->
                    Box(
                        modifier =
                            Modifier.padding(start = 6.dp, top = 1.dp, bottom = 1.dp, end = 6.dp)
                                .size(30.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape,
                                )
                    ) {
                        Text(
                            text = dayOfWeek.name.take(1),
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }

            Row(
                modifier =
                    Modifier.padding(bottom = 16.dp, end = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.medium,
                        )
            ) {
                HeatMapCalendar(
                    state = heatMapState,
                    contentPadding = PaddingValues(8.dp),
                    monthHeader = {
                        Box(modifier = Modifier.padding(2.dp)) {
                            Text(
                                text =
                                    it.yearMonth.format(
                                        YearMonth.Format {
                                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                                            chars(" ")
                                            year()
                                        }
                                    ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    },
                    dayContent = { day, _ ->
                        val count = heatMapData[day.date]

                        Box(
                            modifier =
                                Modifier.padding(1.dp)
                                    .size(30.dp)
                                    .background(
                                        shape = MaterialTheme.shapes.small,
                                        color =
                                            when (count) {
                                                0 ->
                                                    MaterialTheme.colorScheme
                                                        .surfaceContainerHighest
                                                null ->
                                                    MaterialTheme.colorScheme.surfaceContainerLowest
                                                else ->
                                                    MaterialTheme.colorScheme.primary.copy(
                                                        alpha =
                                                            (count.toFloat() / totalHabits)
                                                                .coerceIn(0f, 1f)
                                                    )
                                            },
                                    )
                        )
                    },
                )
            }
        }
    }
}

@AllPreviews
@Composable
private fun Preview() {
    GritTheme(theme = Theme(appTheme = AppTheme.SYSTEM, seedColor = Color.Green)) {
        HabitHeatMap(
            heatMapState =
                rememberHeatMapCalendarState(
                    startMonth = YearMonth.now().minus(1, DateTimeUnit.YEAR),
                    endMonth = YearMonth.now(),
                    firstVisibleMonth = YearMonth.now(),
                    firstDayOfWeek = DayOfWeek.MONDAY,
                ),
            heatMapData =
                (0..100).associate { it ->
                    LocalDate.now().minus(it, DateTimeUnit.DAY) to Random.nextInt(0, 11)
                },
            totalHabits = 10,
        )
    }
}
