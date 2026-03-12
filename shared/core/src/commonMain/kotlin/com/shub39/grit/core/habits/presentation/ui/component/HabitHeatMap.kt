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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.shared_ui.endItemShape
import com.shub39.grit.core.shared_ui.leadingItemShape
import com.shub39.grit.core.shared_ui.listItemColors
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import com.shub39.grit.core.theme.flexFontRounded
import com.shub39.grit.core.utils.AllPreviews
import com.shub39.grit.core.utils.toFormattedString
import grit.shared.core.generated.resources.Res
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

/** Habit heat map, Github contributions style map showing no of habits completed in a day */
@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    totalHabits: Int,
    completedHabits: List<String>,
    updateCompletedHabits: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()
    var selectedDay by remember { mutableStateOf(today) }

    LaunchedEffect(selectedDay) { updateCompletedHabits(selectedDay) }

    AnalyticsCard(
        title = stringResource(Res.string.habit_map),
        icon = Res.drawable.map,
        modifier =
            modifier.animateContentSize(
                animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
            ),
        header = {
            CardArrows(
                onBackAction = { scope.launch { heatMapState.animateScrollBy(-150f) } },
                onForwardAction = { scope.launch { heatMapState.animateScrollBy(150f) } },
            )
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                val days = daysStartingFrom(heatMapState.firstDayOfWeek)

                days.forEachIndexed { index, dayOfWeek ->
                    val shape =
                        when {
                            index == 0 -> leadingItemShape(topRadius = 20)
                            index == days.size - 1 -> endItemShape(bottomRadius = 20)
                            else -> RoundedCornerShape(4.dp)
                        }
                    Box(
                        modifier =
                            Modifier.padding(start = 6.dp, top = 1.dp, bottom = 1.dp, end = 6.dp)
                                .size(30.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = shape,
                                )
                    ) {
                        Text(
                            text = dayOfWeek.name.take(1),
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }

            Row(
                modifier =
                    Modifier.padding(bottom = 8.dp, end = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.medium,
                        )
            ) {
                HeatMapCalendar(
                    state = heatMapState,
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
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
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    },
                    dayContent = { day, _ ->
                        if (day.date > today) return@HeatMapCalendar
                        val count = heatMapData[day.date]

                        Box(
                            modifier =
                                Modifier.padding(1.dp)
                                    .size(30.dp)
                                    .background(
                                        shape = MaterialTheme.shapes.extraSmall,
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
                                    .then(
                                        if (selectedDay == day.date) {
                                            Modifier.border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.extraSmall,
                                            )
                                        } else Modifier
                                    )
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable { selectedDay = day.date }
                        )
                    },
                )
            }
        }

        Text(
            text = selectedDay.toFormattedString(),
            fontFamily = flexFontRounded(),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (completedHabits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            completedHabits.forEachIndexed { index, habit ->
                val shape =
                    when {
                        completedHabits.size == 1 -> RoundedCornerShape(12.dp)
                        index == 0 -> leadingItemShape(topRadius = 12)
                        index == completedHabits.size - 1 -> endItemShape(bottomRadius = 12)
                        else -> RoundedCornerShape(4.dp)
                    }

                ListItem(
                    colors = listItemColors(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp).clip(shape),
                    headlineContent = { Text(habit) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@AllPreviews
@Composable
private fun Preview() {
    GritTheme(theme = Theme(appTheme = AppTheme.SYSTEM, seedColor = Color.Red)) {
        HabitHeatMap(
            heatMapState =
                rememberHeatMapCalendarState(
                    startMonth = YearMonth.now().minus(1, DateTimeUnit.YEAR),
                    endMonth = YearMonth.now(),
                    firstVisibleMonth = YearMonth.now(),
                    firstDayOfWeek = DayOfWeek.MONDAY,
                ),
            heatMapData =
                (0..100).associate {
                    LocalDate.now().minus(it, DateTimeUnit.DAY) to Random.nextInt(0, 11)
                },
            totalHabits = 10,
            completedHabits = listOf("Habit 1", "Habit 2", "Habit ${Random.nextInt()}"),
            updateCompletedHabits = {},
        )
    }
}
