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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.StreakPosition
import com.shub39.grit.core.habits.domain.heatMapStreakShape
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.daysStartingFrom
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.utils.AllPreviews
import com.shub39.grit.core.utils.now
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.view_week
import grit.shared.core.generated.resources.weekly_progress
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(
    ExperimentalTime::class,
    FormatStringsInDatetimeFormats::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun WeeklyBooleanHeatMap(
    heatMapState: HeatMapCalendarState,
    onAction: (HabitsAction) -> Unit,
    habit: Habit,
    statuses: List<HabitStatus>,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    val doneDates = remember(statuses) { statuses.map { it.date.date }.toSet() }
    val edgeWeeks =
        listOf(heatMapState.firstDayOfWeek, daysStartingFrom(heatMapState.firstDayOfWeek).last())

    AnalyticsCard(
        title = stringResource(Res.string.weekly_progress),
        icon = Res.drawable.view_week,
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
                                .size(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = CircleShape,
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
                                            char(' ')
                                            year()
                                        }
                                    ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    },
                    dayContent = { day, _ ->
                        val done = day.date in doneDates
                        val validDay = day.date <= today && day.date.dayOfWeek in habit.days

                        Box(
                            modifier =
                                Modifier.padding(horizontal = 1.dp).size(35.dp).clickable(
                                    enabled = validDay
                                ) {
                                    onAction(HabitsAction.InsertStatus(habit, day.date))
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
                                                    heatMapStreakShape(
                                                        streakPosition = streakPosition,
                                                        isFirstDayOfWeek =
                                                            day.date.dayOfWeek == edgeWeeks.first(),
                                                        isLastDayOfWeek =
                                                            day.date.dayOfWeek == edgeWeeks.last(),
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
                                                Modifier.size(30.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        shape = MaterialShapes.Circle.toShape(),
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = day.date.day.toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = day.date.day.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = day.date.day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =
                                        if (!validDay)
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

@AllPreviews
@Composable
private fun Preview() {
    GritTheme {
        WeeklyBooleanHeatMap(
            heatMapState =
                rememberHeatMapCalendarState(
                    startMonth = YearMonth.now().minus(1, DateTimeUnit.YEAR),
                    endMonth = YearMonth.now(),
                    firstVisibleMonth = YearMonth.now(),
                    firstDayOfWeek = DayOfWeek.MONDAY,
                ),
            onAction = {},
            habit =
                Habit(
                    id = 1,
                    title = "Test Habit",
                    description = "A Test Habit",
                    time = LocalDateTime.now(),
                    days = DayOfWeek.entries.toSet(),
                    index = 1,
                    reminder = false,
                ),
            statuses =
                (0..40).map {
                    HabitStatus(
                        habitId = 1,
                        date = LocalDate.now().minus(it, DateTimeUnit.DAY).atTime(0, 0),
                    )
                },
        )
    }
}
