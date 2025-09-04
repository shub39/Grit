package com.shub39.grit.habits.presentation.ui.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.DensityLarge
import androidx.compose.material.icons.rounded.Expand
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.component.Empty
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.ui.component.HabitCard
import com.shub39.grit.habits.presentation.ui.component.HabitUpsertSheet
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.DayOfWeek
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitsList(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToOverallAnalytics: () -> Unit
) = PageFill {
    var editState by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    var reorderableHabits by remember(state.habitsWithStatuses) {
        mutableStateOf(
            state.habitsWithStatuses.entries.toList()
        )
    }
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            reorderableHabits = reorderableHabits.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LargeFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                Text(text = stringResource(id = R.string.habits))
            },
            subtitle = {
                Column {
                    Text(
                        text = "${state.completedHabits.size}/${state.habitsWithStatuses.size} " + stringResource(
                            R.string.completed
                        )
                    )
                }
            },
            actions = {
                AnimatedVisibility(
                    visible = state.habitsWithStatuses.isNotEmpty()
                ) {
                   Row {
                       FilledTonalIconToggleButton(
                           checked = state.compactHabitView,
                           shapes = IconToggleButtonShapes(
                               shape = CircleShape,
                               checkedShape = MaterialTheme.shapes.small,
                               pressedShape = MaterialTheme.shapes.extraSmall,
                           ),
                           onCheckedChange = {
                               onAction(
                                   HabitsPageAction.OnToggleCompactView(it)
                               )
                           }
                       ) {
                           Icon(
                               imageVector = if (state.compactHabitView) {
                                   Icons.Rounded.Expand
                               } else {
                                   Icons.Rounded.DensityLarge
                               },
                               contentDescription = "Compact View"
                           )
                       }

                       FilledTonalIconToggleButton(
                           checked = editState,
                           shapes = IconToggleButtonShapes(
                               shape = CircleShape,
                               checkedShape = MaterialTheme.shapes.small,
                               pressedShape = MaterialTheme.shapes.extraSmall,
                           ),
                           onCheckedChange = { editState = it },
                           enabled = state.habitsWithStatuses.isNotEmpty()
                       ) {
                           Icon(
                               imageVector = Icons.Rounded.Reorder,
                               contentDescription = null
                           )
                       }
                   }
                }
            }
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // habits
            itemsIndexed(reorderableHabits, key = { _, it -> it.key.id }) { index, habit ->
                ReorderableItem(reorderableListState, key = habit.key.id) {
                    val cardCorners by animateDpAsState(
                        targetValue = if (!it) 20.dp else 16.dp
                    )

                    HabitCard(
                        habit = habit.key,
                        statusList = habit.value,
                        completed = state.completedHabits.contains(habit.key),
                        action = onAction,
                        startingDay = state.startingDay,
                        editState = editState,
                        onNavigateToAnalytics = onNavigateToAnalytics,
                        timeFormat = state.timeFormat,
                        reorderHandle = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                contentDescription = "Drag Indicator",
                                modifier = Modifier.draggableHandle(
                                    onDragStopped = {
                                        onAction(
                                            HabitsPageAction.ReorderHabits(reorderableHabits.mapIndexed { index, entry -> index to entry.key })
                                        )
                                    }
                                )
                            )
                        },
                        shape = RoundedCornerShape(cardCorners),
                        compactView = state.compactHabitView
                    )
                }
            }

            // when no habits
            if (state.habitsWithStatuses.isEmpty()) {
                item { Empty() }
            }

            // ui sweetener
            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = onNavigateToOverallAnalytics,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.animateFloatingActionButton(
                visible = state.habitsWithStatuses.isNotEmpty() && fabVisible,
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
                onAction(HabitsPageAction.OnAddHabitClicked)
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
                    visible = state.habitsWithStatuses.isEmpty()
                ) {
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        }
    }

    // add dialog
    if (state.showHabitAddSheet) {
        HabitUpsertSheet(
            habit = Habit(
                title = "",
                description = "",
                time = LocalDateTime.now(),
                days = DayOfWeek.entries.toSet(),
                index = reorderableHabits.size,
                reminder = false
            ),
            onDismissRequest = { onAction(HabitsPageAction.DismissAddHabitDialog) },
            timeFormat = state.timeFormat,
            onUpsertHabit = { onAction(HabitsPageAction.AddHabit(it)) },
            is24Hr = state.is24Hr,
        )
    }
}