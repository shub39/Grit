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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.component.HabitHeatMap
import com.shub39.grit.core.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.overall_analytics
import kotlin.time.ExperimentalTime
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class,
)
@Composable
fun OverallAnalytics(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPaywall: () -> Unit,
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection).fillMaxSize()) {
        MediumFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
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
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back),
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            item {
                HabitHeatMap(
                    heatMapState = heatMapState,
                    heatMapData = state.overallAnalytics.heatMapData,
                    modifier = Modifier.widthIn(max = maxWidth),
                    totalHabits = state.habitsWithAnalytics.size,
                    completedHabits = state.overallAnalytics.completedHabits,
                    updateCompletedHabits = {
                        onAction(HabitsAction.FetchCompletedHabitsForDate(it))
                    },
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
