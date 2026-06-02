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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import com.shub39.grit.shared.ui.habit.ui.component.stats.HabitHeatMap
import com.shub39.grit.shared.ui.habit.ui.component.stats.WeekDayBreakdown
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun OverallAnalytics(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToCalendarHeatMap: () -> Unit,
    showNavigateBack: Boolean = true,
    isUserSubscribed: Boolean,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current

    val currentMonth = remember { YearMonth.now() }
    val heatMapState =
        rememberHeatMapCalendarState(
            startMonth = currentMonth.minusMonths(12),
            endMonth = currentMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = state.startingDay,
        )

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            colors =
                TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
            title = {
                Text(
                    text = stringResource(Res.string.overall_analytics),
                    fontFamily = flexFontEmphasis(),
                )
            },
            windowInsets =
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    WindowInsets(0)
                } else {
                    TopAppBarDefaults.windowInsets
                },
            navigationIcon = {
                if (showNavigateBack) {
                    FilledTonalIconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.nav_arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                }
            },
        )

        val maxWidth = 380.dp
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = maxWidth),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().widthIn(max = maxWidth),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { state.overallAnalytics.consistency },
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 36.dp,
                            modifier = Modifier.size(200.dp),
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text =
                                    "${(state.overallAnalytics.consistency * 100).roundToInt()}%",
                                style =
                                    MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = flexFontRounded(),
                                        color = MaterialTheme.colorScheme.primary,
                                    ),
                            )
                            Text(
                                text = stringResource(Res.string.consistency),
                                style =
                                    MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = flexFontRounded(),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                            )
                        }
                    }

                    if (state.overallAnalytics.topHabits.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            state.overallAnalytics.topHabits.forEachIndexed { index, habit ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style =
                                            MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = flexFontRounded(),
                                                color = MaterialTheme.colorScheme.primary,
                                            ),
                                    )
                                    Column {
                                        Text(
                                            text = habit.title,
                                            style =
                                                MaterialTheme.typography.titleSmall.copy(
                                                    fontFamily = flexFontRounded(),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = "${(habit.consistency * 100).roundToInt()}%",
                                            style =
                                                MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = flexFontRounded(),
                                                    color =
                                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                HabitHeatMap(
                    heatMapState = heatMapState,
                    heatMapData = state.overallAnalytics.heatMapData,
                    modifier = Modifier.widthIn(max = maxWidth),
                    totalHabits = state.habitsWithAnalytics.size,
                    onNavigateToCalendarHeatMap = onNavigateToCalendarHeatMap,
                )
            }

            item {
                WeekDayBreakdown(
                    canSeeContent = isUserSubscribed,
                    weekDayData = state.overallAnalytics.weekDayFrequencyData,
                    onNavigateToPaywall = onNavigateToPaywall,
                    modifier = Modifier.widthIn(max = maxWidth),
                )
            }
        }
    }
}
