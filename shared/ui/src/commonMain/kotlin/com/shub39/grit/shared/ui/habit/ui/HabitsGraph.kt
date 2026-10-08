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
package com.shub39.grit.shared.ui.habit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isExpanded
import com.shub39.grit.shared.ui.components.PageFill
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import com.shub39.grit.shared.ui.habit.ui.component.HabitListFABs
import com.shub39.grit.shared.ui.habit.ui.sections.AnalyticsPage
import com.shub39.grit.shared.ui.habit.ui.sections.Calendar
import com.shub39.grit.shared.ui.habit.ui.sections.CalendarHeatMap
import com.shub39.grit.shared.ui.habit.ui.sections.OverallAnalytics
import com.shub39.grit.shared.ui.navigation.horizontalTransitionMetadata
import com.shub39.grit.shared.ui.navigation.verticalTransitionMetadata
import grit.shared.ui.generated.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
private sealed interface HabitRoutes : NavKey {
    @Serializable data object HabitList : HabitRoutes

    @Serializable data object HabitAnalytics : HabitRoutes

    @Serializable data object OverallAnalytics : HabitRoutes

    @Serializable data object Calendar : HabitRoutes

    @Serializable data object CalendarHeatMap : HabitRoutes
}

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HabitRoutes.HabitList::class, HabitRoutes.HabitList.serializer())
            subclass(HabitRoutes.HabitAnalytics::class, HabitRoutes.HabitAnalytics.serializer())
            subclass(HabitRoutes.OverallAnalytics::class, HabitRoutes.OverallAnalytics.serializer())
            subclass(HabitRoutes.Calendar::class, HabitRoutes.Calendar.serializer())
            subclass(HabitRoutes.CalendarHeatMap::class, HabitRoutes.CalendarHeatMap.serializer())
        }
    }
}

@Composable
fun HabitsGraph(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToPaywall: () -> Unit,
    isUserSubscribed: Boolean,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) { onAction(HabitsAction.OnHabitsOpened) }

    if (!windowSizeClass.isExpanded()) {
        val backstack = rememberNavBackStack(config, HabitRoutes.HabitList)

        NavDisplay(
            modifier = modifier,
            backStack = backstack,
            entryProvider =
                entryProvider {
                    entry<HabitRoutes.HabitList> {
                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            PageFill {
                                val lazyListState = rememberLazyListState()
                                val fabVisible by remember {
                                    derivedStateOf {
                                        lazyListState.firstVisibleItemIndex == 0 &&
                                            lazyListState.firstVisibleItemScrollOffset == 0
                                    }
                                }

                                HabitListFABs(
                                    onNavigateToOverallAnalytics = {
                                        backstack.add(HabitRoutes.OverallAnalytics)
                                    },
                                    state = state,
                                    fabVisible = fabVisible && !state.editState,
                                    onAction = onAction,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                    isUserSubscribed = isUserSubscribed,
                                )
                            }
                        }
                    }

                    entry<HabitRoutes.HabitAnalytics>(metadata = horizontalTransitionMetadata()) {
                        AnalyticsPage(
                            state = state,
                            onAction = onAction,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            onNavigateToPaywall = onNavigateToPaywall,
                            onNavigateToCalendar = { backstack.add(HabitRoutes.Calendar) },
                            isUserSubscribed = isUserSubscribed,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        )
                    }

                    entry<HabitRoutes.OverallAnalytics>(metadata = verticalTransitionMetadata()) {
                        LaunchedEffect(Unit) { onAction(HabitsAction.OnOverallAnalyticsViewed) }
                        OverallAnalytics(
                            state = state,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            onNavigateToPaywall = onNavigateToPaywall,
                            isUserSubscribed = isUserSubscribed,
                            onAction = onAction,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onNavigateToCalendarHeatMap = {
                                backstack.add(HabitRoutes.CalendarHeatMap)
                            },
                        )
                    }

                    entry<HabitRoutes.Calendar>(metadata = horizontalTransitionMetadata()) {
                        Calendar(
                            state = state,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            onDateClick = { habit, date ->
                                onAction(HabitsAction.InsertStatus(habit, date))
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        )
                    }

                    entry<HabitRoutes.CalendarHeatMap>(metadata = horizontalTransitionMetadata()) {
                        CalendarHeatMap(
                            state = state,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            onChangeSelectedDay = {
                                onAction(HabitsAction.FetchCompletedHabitsForDate(it))
                            },
                        )
                    }
                },
        )
    } else {
        ExpandedScreen(
            modifier = modifier,
            state = state,
            onAction = onAction,
            scrollBehavior = scrollBehavior,
            onNavigateToPaywall = onNavigateToPaywall,
            isUserSubscribed = isUserSubscribed,
        )
    }
}

@Composable
private fun ExpandedScreen(
    modifier: Modifier,
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateToPaywall: () -> Unit,
    isUserSubscribed: Boolean,
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.weight(1f)) {
            Box {
                val lazyListState = rememberLazyListState()
                val fabVisible by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex == 0 &&
                            lazyListState.firstVisibleItemScrollOffset == 0
                    }
                }

                HabitListFABs(
                    onNavigateToOverallAnalytics = {
                        onAction(HabitsAction.PrepareAnalytics(null))
                    },
                    state = state,
                    fabVisible = fabVisible,
                    onAction = onAction,
                    onNavigateToPaywall = onNavigateToPaywall,
                    isUserSubscribed = isUserSubscribed,
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(topStart = 28.dp),
                modifier = Modifier.weight(1f),
            ) {
                val backstack = rememberNavBackStack(config, HabitRoutes.HabitAnalytics)

                LaunchedEffect(state.analyticsHabitId) {
                    backstack.add(
                        if (state.analyticsHabitId != null) {
                            HabitRoutes.HabitAnalytics
                        } else {
                            HabitRoutes.OverallAnalytics
                        }
                    )
                }

                NavDisplay(
                    backStack = backstack,
                    entryProvider =
                        entryProvider {
                            entry<HabitRoutes.HabitAnalytics>(
                                metadata = verticalTransitionMetadata()
                            ) {
                                AnalyticsPage(
                                    state = state,
                                    onAction = onAction,
                                    onNavigateBack = {
                                        onAction(HabitsAction.PrepareAnalytics(null))
                                    },
                                    onNavigateToPaywall = onNavigateToPaywall,
                                    onNavigateToCalendar = { backstack.add(HabitRoutes.Calendar) },
                                    isUserSubscribed = isUserSubscribed,
                                    modifier =
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        ),
                                )
                            }

                            entry<HabitRoutes.Calendar>(metadata = horizontalTransitionMetadata()) {
                                Calendar(
                                    state = state,
                                    onNavigateBack = {
                                        if (backstack.size != 1) backstack.removeLastOrNull()
                                    },
                                    onDateClick = { habit, date ->
                                        onAction(HabitsAction.InsertStatus(habit, date))
                                    },
                                    modifier =
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        ),
                                )
                            }

                            entry<HabitRoutes.OverallAnalytics>(
                                metadata = verticalTransitionMetadata()
                            ) {
                                OverallAnalytics(
                                    state = state,
                                    onNavigateBack = {},
                                    showNavigateBack = false,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                    isUserSubscribed = isUserSubscribed,
                                    onAction = onAction,
                                    onNavigateToCalendarHeatMap = {
                                        backstack.add(HabitRoutes.CalendarHeatMap)
                                    },
                                    modifier =
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        ),
                                )
                            }

                            entry<HabitRoutes.CalendarHeatMap>(
                                metadata = horizontalTransitionMetadata()
                            ) {
                                CalendarHeatMap(
                                    state = state,
                                    onNavigateBack = {
                                        if (backstack.size != 1) backstack.removeLastOrNull()
                                    },
                                    modifier =
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        ),
                                    onChangeSelectedDay = {
                                        onAction(HabitsAction.FetchCompletedHabitsForDate(it))
                                    },
                                )
                            }
                        },
                )
            }
        }
    }
}
