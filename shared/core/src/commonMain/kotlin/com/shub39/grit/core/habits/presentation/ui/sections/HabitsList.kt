package com.shub39.grit.core.habits.presentation.ui.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.component.HabitCard
import com.shub39.grit.core.habits.presentation.ui.component.HabitUpsertSheet
import com.shub39.grit.core.shared_ui.Empty
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add_habit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun HabitsList(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToOverallAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) = PageFill(Modifier) {
    val windowSizeClass = LocalWindowSizeClass.current

    val lazyListState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    var reorderableHabits by remember(state.habitsWithAnalytics) {
        mutableStateOf(state.habitsWithAnalytics)
    }
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            reorderableHabits = reorderableHabits.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }


    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            // habits
            items(reorderableHabits, key = { it.habit.id }) { habitWithAnalytics ->
                ReorderableItem(reorderableListState, key = habitWithAnalytics.habit.id) {
                    val cardCorners by animateDpAsState(
                        targetValue = if (!it) 20.dp else 16.dp
                    )

                    HabitCard(
                        habitWithAnalytics = habitWithAnalytics,
                        completed = state.completedHabitIds.contains(habitWithAnalytics.habit.id),
                        action = onAction,
                        startingDay = state.startingDay,
                        editState = state.editState,
                        onNavigateToAnalytics = onNavigateToAnalytics,
                        is24Hr = state.is24Hr,
                        reorderHandle = {
                            Icon(
                                imageVector = Icons.Rounded.DragIndicator,
                                contentDescription = "Drag Indicator",
                                modifier = Modifier.draggableHandle(
                                    onDragStopped = {
                                        onAction(
                                            HabitsAction.ReorderHabits(reorderableHabits.mapIndexed { index, entry -> index to entry })
                                        )
                                    }
                                )
                            )
                        },
                        shape = RoundedCornerShape(cardCorners),
                        compactView = state.compactHabitView,
                        analyticsEnabled = state.analyticsHabitId != habitWithAnalytics.habit.id || windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded
                    )
                }
            }

            // when no habits
            if (state.habitsWithAnalytics.isEmpty()) {
                item { Empty(Modifier.padding(top = 150.dp)) }
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .align(
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    Alignment.BottomStart
                } else {
                    Alignment.BottomEnd
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
            FloatingActionButton(
                onClick = onNavigateToOverallAnalytics,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.animateFloatingActionButton(
                    visible = state.habitsWithAnalytics.isNotEmpty() && fabVisible,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Analytics,
                    contentDescription = "All Analytics"
                )
            }

            MediumFloatingActionButton(
                onClick = {
                    onAction(HabitsAction.OnAddHabitClicked)
                },
                modifier = Modifier.animateFloatingActionButton(
                    visible = fabVisible,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Habit",
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
                    )

                    AnimatedVisibility(
                        visible = state.habitsWithAnalytics.isEmpty()
                    ) {
                        Text(text = stringResource(Res.string.add_habit))
                    }
                }
            }
        } else {
            MediumFloatingActionButton(
                onClick = {
                    onAction(HabitsAction.OnAddHabitClicked)
                },
                modifier = Modifier.animateFloatingActionButton(
                    visible = fabVisible,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Habit",
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
                    )

                    AnimatedVisibility(
                        visible = state.habitsWithAnalytics.isEmpty()
                    ) {
                        Text(text = stringResource(Res.string.add_habit))
                    }
                }
            }

            FloatingActionButton(
                onClick = onNavigateToOverallAnalytics,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.animateFloatingActionButton(
                    visible = state.habitsWithAnalytics.isNotEmpty() && fabVisible,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Analytics,
                    contentDescription = "All Analytics"
                )
            }
        }
    }

    // add dialog
    if (state.showHabitAddSheet) {
        HabitUpsertSheet(
            habit = Habit(
                title = "",
                description = "",
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                days = DayOfWeek.entries.toSet(),
                index = reorderableHabits.size,
                reminder = false
            ),
            onDismissRequest = { onAction(HabitsAction.DismissAddHabitDialog) },
            onUpsertHabit = { onAction(HabitsAction.AddHabit(it)) },
            is24Hr = state.is24Hr,
        )
    }
}