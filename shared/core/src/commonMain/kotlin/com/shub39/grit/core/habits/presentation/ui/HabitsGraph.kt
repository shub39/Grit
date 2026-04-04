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
package com.shub39.grit.core.habits.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.component.HabitListFABs
import com.shub39.grit.core.habits.presentation.ui.sections.AnalyticsPage
import com.shub39.grit.core.habits.presentation.ui.sections.HabitsList
import com.shub39.grit.core.habits.presentation.ui.sections.OverallAnalytics
import com.shub39.grit.core.navigation.horizontalTransitionMetadata
import com.shub39.grit.core.navigation.verticalTransitionMetadata
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.theme.flexFontRounded
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.collapse
import grit.shared.core.generated.resources.completed
import grit.shared.core.generated.resources.expand
import grit.shared.core.generated.resources.habits
import grit.shared.core.generated.resources.reorder
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable data object HabitList : NavKey

@Serializable data object HabitAnalytics : NavKey

@Serializable data object OverallAnalytics : NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HabitList::class, HabitList.serializer())
            subclass(HabitAnalytics::class, HabitAnalytics.serializer())
            subclass(OverallAnalytics::class, OverallAnalytics.serializer())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
        val backstack = rememberNavBackStack(config, HabitList)

        NavDisplay(
            modifier = modifier,
            backStack = backstack,
            entryProvider =
                entryProvider {
                    entry<HabitList> {
                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            HabitsTopAppBar(
                                state = state,
                                onAction = onAction,
                                scrollBehavior = scrollBehavior,
                            )

                            PageFill {
                                val lazyListState = rememberLazyListState()
                                val fabVisible by remember {
                                    derivedStateOf { lazyListState.firstVisibleItemIndex == 0 }
                                }

                                HabitsList(
                                    state = state,
                                    onAction = onAction,
                                    lazyListState = lazyListState,
                                    onNavigateToAnalytics = { backstack.add(HabitAnalytics) },
                                    modifier =
                                        Modifier.fillMaxHeight()
                                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                                )

                                HabitListFABs(
                                    onNavigateToOverallAnalytics = {
                                        backstack.add(OverallAnalytics)
                                    },
                                    state = state,
                                    fabVisible = fabVisible,
                                    onAction = onAction,
                                    onNavigateToPaywall = onNavigateToPaywall,
                                    isUserSubscribed = isUserSubscribed,
                                )
                            }
                        }
                    }

                    entry<HabitAnalytics>(metadata = horizontalTransitionMetadata()) {
                        AnalyticsPage(
                            state = state,
                            onAction = onAction,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            onNavigateToPaywall = onNavigateToPaywall,
                            isUserSubscribed = isUserSubscribed,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        )
                    }

                    entry<OverallAnalytics>(metadata = verticalTransitionMetadata()) {
                        OverallAnalytics(
                            state = state,
                            onNavigateBack = {
                                if (backstack.size != 1) backstack.removeLastOrNull()
                            },
                            onNavigateToPaywall = onNavigateToPaywall,
                            isUserSubscribed = isUserSubscribed,
                            onAction = onAction,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        )
                    }
                },
        )
    } else {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            HabitsTopAppBar(state = state, onAction = onAction, scrollBehavior = scrollBehavior)

            Row(modifier = Modifier.weight(1f)) {
                Box {
                    val lazyListState = rememberLazyListState()
                    val fabVisible by remember {
                        derivedStateOf { lazyListState.firstVisibleItemIndex == 0 }
                    }

                    HabitsList(
                        state = state,
                        onAction = onAction,
                        onNavigateToAnalytics = {},
                        lazyListState = lazyListState,
                        modifier =
                            Modifier.fillMaxHeight()
                                .widthIn(max = 400.dp)
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                    )

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
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(topStart = 28.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    AnimatedContent(targetState = state.analyticsHabitId) {
                        if (it != null) {
                            AnalyticsPage(
                                state = state,
                                onAction = onAction,
                                onNavigateBack = { onAction(HabitsAction.PrepareAnalytics(null)) },
                                onNavigateToPaywall = onNavigateToPaywall,
                                isUserSubscribed = isUserSubscribed,
                                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            )
                        } else {
                            OverallAnalytics(
                                state = state,
                                onNavigateBack = {},
                                showNavigateBack = false,
                                onNavigateToPaywall = onNavigateToPaywall,
                                isUserSubscribed = isUserSubscribed,
                                onAction = onAction,
                                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HabitsTopAppBar(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeFlexibleTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors =
            TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
        title = { Text(text = stringResource(Res.string.habits), fontFamily = flexFontEmphasis()) },
        subtitle = {
            Column {
                Text(
                    text =
                        "${state.completedHabitIds.size}/${state.habitsWithAnalytics.size} " +
                            stringResource(Res.string.completed),
                    fontFamily = flexFontRounded(),
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = state.habitsWithAnalytics.isNotEmpty()) {
                Row {
                    FilledTonalIconToggleButton(
                        checked = state.compactHabitView,
                        shapes =
                            IconToggleButtonShapes(
                                shape = CircleShape,
                                checkedShape = MaterialTheme.shapes.small,
                                pressedShape = MaterialTheme.shapes.extraSmall,
                            ),
                        onCheckedChange = { onAction(HabitsAction.OnToggleCompactView(it)) },
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    if (state.compactHabitView) {
                                        Res.drawable.expand
                                    } else {
                                        Res.drawable.collapse
                                    }
                                ),
                            contentDescription = "Compact View",
                        )
                    }

                    FilledTonalIconToggleButton(
                        checked = state.editState,
                        shapes =
                            IconToggleButtonShapes(
                                shape = CircleShape,
                                checkedShape = MaterialTheme.shapes.small,
                                pressedShape = MaterialTheme.shapes.extraSmall,
                            ),
                        onCheckedChange = { onAction(HabitsAction.OnToggleEditState(it)) },
                        enabled = state.habitsWithAnalytics.isNotEmpty(),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.reorder),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
    )
}
