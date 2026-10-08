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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.now
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isExpanded
import com.shub39.grit.shared.ui.components.Empty
import com.shub39.grit.shared.ui.components.PageFill
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import com.shub39.grit.shared.ui.habit.ui.component.HabitCard
import com.shub39.grit.shared.ui.habit.ui.component.HabitListFABs
import com.shub39.grit.shared.ui.habit.ui.component.HabitUpsertSheet
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.collapse
import grit.shared.ui.generated.resources.completed
import grit.shared.ui.generated.resources.drag_indicator
import grit.shared.ui.generated.resources.expand
import grit.shared.ui.generated.resources.habits
import grit.shared.ui.generated.resources.reorder
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HabitsList(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToOverallAnalytics: () -> Unit,
    isUserSubscribed: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier.fillMaxSize()) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Column(modifier = Modifier.widthIn(max = 700.dp)) {
            HabitsTopAppBar(state = state, onAction = onAction, scrollBehavior = scrollBehavior)

            PageFill {
                val lazyListState = rememberLazyListState()
                val fabVisible by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex == 0 &&
                            lazyListState.firstVisibleItemScrollOffset == 0
                    }
                }

                HabitsListContent(
                    state = state,
                    onAction = onAction,
                    lazyListState = lazyListState,
                    onNavigateToAnalytics = onNavigateToAnalytics,
                    modifier =
                        Modifier.fillMaxHeight().nestedScroll(scrollBehavior.nestedScrollConnection),
                )

                HabitListFABs(
                    onNavigateToOverallAnalytics = onNavigateToOverallAnalytics,
                    state = state,
                    fabVisible = fabVisible && !state.editState,
                    onAction = onAction,
                    onNavigateToPaywall = onNavigateToPaywall,
                    isUserSubscribed = isUserSubscribed,
                )
            }
        }
    }

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
            AnimatedVisibility(
                visible = state.habitsWithAnalytics.isNotEmpty(),
                enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
            ) {
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

@Composable
private fun HabitsListContent(
    state: HabitState,
    lazyListState: LazyListState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            onAction(HabitsAction.OnTransientHabitReorder(from.index, to.index))
        }

    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxHeight(),
        ) {
            // habits
            itemsIndexed(state.habitsWithAnalytics, key = { _, it -> it.habit.id }) {
                index,
                habitWithAnalytics ->
                ReorderableItem(reorderableListState, key = habitWithAnalytics.habit.id) {
                    val completed = state.completedHabitIds.contains(habitWithAnalytics.habit.id)
                    val shape =
                        when {
                            state.habitsWithAnalytics.size == 1 || !completed ->
                                detachedItemShape(radius = 28)
                            index == 0 -> leadingItemShape(topRadius = 28, bottomRadius = 8)
                            index == state.habitsWithAnalytics.size - 1 ->
                                endItemShape(bottomRadius = 28, topRadius = 8)
                            else -> middleItemShape(radius = 8)
                        }

                    HabitCard(
                        habitWithAnalytics = habitWithAnalytics,
                        completed = completed,
                        action = onAction,
                        startingDay = state.startingDay,
                        editState = state.editState,
                        onNavigateToAnalytics = onNavigateToAnalytics,
                        is24Hr = state.is24Hr,
                        reorderHandle = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.drag_indicator),
                                contentDescription = "Drag Indicator",
                                modifier =
                                    Modifier.draggableHandle(
                                        onDragStopped = { onAction(HabitsAction.ReorderHabits) }
                                    ),
                            )
                        },
                        shape = shape,
                        compactView = state.compactHabitView,
                        analyticsEnabled =
                            state.analyticsHabitId != habitWithAnalytics.habit.id ||
                                !windowSizeClass.isExpanded(),
                    )
                }
            }

            // when no habits
            if (state.habitsWithAnalytics.isEmpty()) {
                item {
                    Empty(
                        modifier = Modifier.padding(top = 150.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }

    // add dialog
    if (state.showHabitAddSheet) {
        HabitUpsertSheet(
            habit =
                Habit(
                    title = "",
                    description = "",
                    time = LocalDateTime.now(),
                    days = DayOfWeek.entries.toSet(),
                    index = state.habitsWithAnalytics.size,
                    reminder = false,
                ),
            onDismissRequest = { onAction(HabitsAction.DismissAddHabitDialog) },
            onUpsertHabit = { onAction(HabitsAction.AddHabit(it)) },
            is24Hr = state.is24Hr,
        )
    }
}
