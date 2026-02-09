package com.shub39.grit.core.habits.presentation.ui.sections

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.component.HabitCard
import com.shub39.grit.core.habits.presentation.ui.component.HabitUpsertSheet
import com.shub39.grit.core.shared_ui.Empty
import com.shub39.grit.core.utils.LocalWindowSizeClass
import com.shub39.grit.core.utils.now
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.drag_indicator
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun HabitsList(
    state: HabitState,
    lazyListState: LazyListState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            // habits
            items(state.habitsWithAnalytics, key = { it.habit.id }) { habitWithAnalytics ->
                ReorderableItem(reorderableListState, key = habitWithAnalytics.habit.id) {
                    val cardCorners by animateDpAsState(
                        targetValue = if (!it) 32.dp else 16.dp
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
                                imageVector = vectorResource(Res.drawable.drag_indicator),
                                contentDescription = "Drag Indicator",
                                modifier = Modifier.draggableHandle(
                                    onDragStopped = { onAction(HabitsAction.ReorderHabits) }
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
                item {
                    Empty(
                        modifier = Modifier.padding(top = 150.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                index = state.habitsWithAnalytics.size,
                reminder = false
            ),
            onDismissRequest = { onAction(HabitsAction.DismissAddHabitDialog) },
            onUpsertHabit = { onAction(HabitsAction.AddHabit(it)) },
            is24Hr = state.is24Hr,
        )
    }
}