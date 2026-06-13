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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.toFormattedString
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.habit.daysStartingFrom
import com.shub39.grit.shared.ui.habit.ui.component.AnalyticsCard
import com.shub39.grit.shared.ui.habit.ui.component.CardArrows
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import kotlin.collections.forEachIndexed
import kotlin.random.Random
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.stringResource

/** Habit heat map, GitHub contributions style map showing no of habits completed in a day */
@Composable
fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    totalHabits: Int,
    onNavigateToCalendarHeatMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()

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
                onExpandAction = onNavigateToCalendarHeatMap,
            )
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                val days = daysStartingFrom(heatMapState.firstDayOfWeek)

                days.forEachIndexed { index, dayOfWeek ->
                    val shape =
                        when (index) {
                            0 -> leadingItemShape(topRadius = 20)
                            days.size - 1 -> endItemShape(bottomRadius = 20)
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
                                text = it.yearMonth.toFormattedString(),
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = flexFontRounded(),
                                    ),
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
                                    .clip(MaterialTheme.shapes.extraSmall)
                        )
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@PreviewWrapper(GritPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
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
        onNavigateToCalendarHeatMap = {},
    )
}
