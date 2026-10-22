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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitCategory
import com.shub39.grit.core.habits.HabitWithAnalytics
import com.shub39.grit.core.now
import com.shub39.grit.core.tasks.CategoryColors
import com.shub39.grit.shared.ui.components.Empty
import com.shub39.grit.shared.ui.components.GritDialog
import com.shub39.grit.shared.ui.components.PageFill
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.genericSaver
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.habit.HabitFilter
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import com.shub39.grit.shared.ui.habit.ui.component.HabitCard
import com.shub39.grit.shared.ui.habit.ui.component.HabitCategoryUpsertSheet
import com.shub39.grit.shared.ui.habit.ui.component.HabitListFABs
import com.shub39.grit.shared.ui.habit.ui.component.HabitStatusDialog
import com.shub39.grit.shared.ui.habit.ui.component.HabitUpsertSheet
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.add
import grit.shared.ui.generated.resources.all
import grit.shared.ui.generated.resources.cancel
import grit.shared.ui.generated.resources.collapse
import grit.shared.ui.generated.resources.completed
import grit.shared.ui.generated.resources.delete
import grit.shared.ui.generated.resources.delete_habit_category
import grit.shared.ui.generated.resources.drag_indicator
import grit.shared.ui.generated.resources.edit
import grit.shared.ui.generated.resources.edit_categories
import grit.shared.ui.generated.resources.expand
import grit.shared.ui.generated.resources.habits
import grit.shared.ui.generated.resources.reorder
import grit.shared.ui.generated.resources.today
import grit.shared.ui.generated.resources.warning
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Serializable
private data class AddMeasuredHabitStatus(val habit: Habit, val date: LocalDate, val value: Double)

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

        var showCategoryAddSheet by rememberSaveable { mutableStateOf(false) }
        var showCategoryEditor by rememberSaveable { mutableStateOf(false) }

        val filteredHabits =
            remember(state.habitsWithAnalytics, state.selectedFilter) {
                state.habitsWithAnalytics.filterBy(state.selectedFilter)
            }

        Column(modifier = Modifier.widthIn(max = 700.dp)) {
            HabitsTopAppBar(
                state = state,
                filteredHabits = filteredHabits,
                onAction = onAction,
                scrollBehavior = scrollBehavior,
            )

            HabitCategorySelector(
                state = state,
                onAction = onAction,
                onAddCategoryClick = {
                    onAction(HabitsAction.OnHabitCategorySheetOpened)
                    showCategoryAddSheet = true
                },
                onEditCategoriesClick = {
                    onAction(HabitsAction.OnHabitCategorySheetOpened)
                    showCategoryEditor = true
                },
            )

            PageFill {
                val lazyListState = rememberLazyListState()
                val fabVisible by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex == 0 &&
                            lazyListState.firstVisibleItemScrollOffset == 0
                    }
                }

                HabitsListContent(
                    habits = filteredHabits,
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

        if (showCategoryAddSheet) {
            HabitCategoryUpsertSheet(
                category = HabitCategory(name = "", color = CategoryColors.GRAY.color),
                onDismiss = {
                    onAction(HabitsAction.OnHabitCategorySheetDismissed)
                    showCategoryAddSheet = false
                },
                onUpsertCategory = {
                    onAction(HabitsAction.AddHabitCategory(it))
                    onAction(HabitsAction.OnHabitCategorySheetDismissed)
                    showCategoryAddSheet = false
                },
            )
        }

        if (showCategoryEditor) {
            HabitCategoryEditDialog(
                state = state,
                onAction = onAction,
                onDismissRequest = {
                    onAction(HabitsAction.OnHabitCategorySheetDismissed)
                    showCategoryEditor = false
                },
            )
        }
    }

@Composable
private fun HabitsTopAppBar(
    state: HabitState,
    filteredHabits: List<HabitWithAnalytics>,
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
                        "${filteredHabits.count { state.completedHabitIds.contains(it.habit.id) }}" +
                            "/${filteredHabits.size} " +
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
    habits: List<HabitWithAnalytics>,
    state: HabitState,
    lazyListState: LazyListState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            onAction(HabitsAction.OnTransientHabitReorder(from.index, to.index))
        }
    var showMeasuredHabitStatusAddDialog: AddMeasuredHabitStatus? by
        rememberSaveable(stateSaver = genericSaver<AddMeasuredHabitStatus?>()) {
            mutableStateOf(null)
        }

    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxHeight(),
        ) {
            // habits
            itemsIndexed(habits, key = { _, it -> it.habit.id }) { index, habitWithAnalytics ->
                ReorderableItem(reorderableListState, key = habitWithAnalytics.habit.id) {
                    val completed = state.completedHabitIds.contains(habitWithAnalytics.habit.id)
                    val shape =
                        when {
                            habits.size == 1 || !completed -> detachedItemShape(radius = 28)
                            index == 0 -> leadingItemShape(topRadius = 28, bottomRadius = 8)
                            index == habits.size - 1 ->
                                endItemShape(bottomRadius = 28, topRadius = 8)
                            else -> middleItemShape(radius = 8)
                        }

                    HabitCard(
                        habitWithAnalytics = habitWithAnalytics,
                        completed = completed,
                        onInsertStatus = { habit, date, value ->
                            if (habitWithAnalytics.habit.isMeasurable) {
                                showMeasuredHabitStatusAddDialog =
                                    AddMeasuredHabitStatus(habit, date, value)
                            } else onAction(HabitsAction.InsertStatus(habit, date))
                        },
                        onPrepareAnalytics = { onAction(HabitsAction.PrepareAnalytics(it)) },
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
                    )
                }
            }

            // when no habits
            if (habits.isEmpty()) {
                item {
                    Empty(
                        modifier = Modifier.padding(top = 150.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }

    // habit add dialog
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
                    categoryId = (state.selectedFilter as? HabitFilter.Category)?.category?.id,
                ),
            categories = state.habitCategories,
            onDismissRequest = { onAction(HabitsAction.DismissAddHabitDialog) },
            onUpsertHabit = { onAction(HabitsAction.AddHabit(it)) },
            is24Hr = state.is24Hr,
        )
    }

    // add measured habit status
    showMeasuredHabitStatusAddDialog?.let { measuredHabitStatus ->
        HabitStatusDialog(
            onDismissRequest = { showMeasuredHabitStatusAddDialog = null },
            habit = measuredHabitStatus.habit,
            value = measuredHabitStatus.value,
            onAddStatus = { value ->
                onAction(
                    HabitsAction.InsertMeasuredStatus(
                        habit = measuredHabitStatus.habit,
                        date = measuredHabitStatus.date,
                        value = value,
                    )
                )
            },
        )
    }
}

private fun List<HabitWithAnalytics>.filterBy(habitFilter: HabitFilter): List<HabitWithAnalytics> =
    when (habitFilter) {
        HabitFilter.All -> this
        HabitFilter.Today -> filter { LocalDate.now().dayOfWeek in it.habit.days }
        is HabitFilter.Category -> filter { it.habit.categoryId == habitFilter.category.id }
    }

@Composable
private fun HabitCategorySelector(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onAddCategoryClick: () -> Unit,
    onEditCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    ) {
        item {
            ToggleButton(
                checked = state.selectedFilter == HabitFilter.Today,
                onCheckedChange = { onAction(HabitsAction.ChangeFilter(HabitFilter.Today)) },
            ) {
                Text(text = stringResource(Res.string.today))
            }
        }

        item {
            ToggleButton(
                checked = state.selectedFilter == HabitFilter.All,
                onCheckedChange = { onAction(HabitsAction.ChangeFilter(HabitFilter.All)) },
            ) {
                Text(text = stringResource(Res.string.all))
            }
        }

        items(state.habitCategories, key = { it.id }) { category ->
            ToggleButton(
                checked = state.selectedFilter == HabitFilter.Category(category),
                onCheckedChange = {
                    onAction(HabitsAction.ChangeFilter(HabitFilter.Category(category)))
                },
            ) {
                Text(text = category.name)
            }
        }

        item {
            Spacer(modifier = Modifier.width(4.dp))
            FilledTonalIconButton(onClick = onAddCategoryClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.add),
                    contentDescription = "Add Category",
                )
            }
            FilledTonalIconButton(onClick = onEditCategoriesClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.edit),
                    contentDescription = "Edit Categories",
                )
            }
        }
    }
}

@Composable
private fun HabitCategoryEditDialog(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
    GritDialog(onDismissRequest = onDismissRequest, padding = 0.dp) {
        var categories by remember(state.habitCategories) { mutableStateOf(state.habitCategories) }

        val listState = rememberLazyListState()
        val reorderableListState =
            rememberReorderableLazyListState(listState) { from, to ->
                categories =
                    categories.toMutableList().apply { add(to.index, removeAt(from.index)) }

                onAction(
                    HabitsAction.ReorderHabitCategories(
                        categories.mapIndexed { index, category -> index to category }
                    )
                )
            }

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                        ),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.edit),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Text(
                text = stringResource(Res.string.edit_categories),
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontEmphasis()),
            )

            LazyColumn(
                modifier =
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                itemsIndexed(categories, key = { _, it -> it.id }) { index, category ->
                    var showEditSheet by rememberSaveable { mutableStateOf(false) }
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                    ReorderableItem(reorderableListState, key = category.id) {
                        val shape =
                            when {
                                categories.size == 1 -> detachedItemShape()
                                index == 0 -> leadingItemShape()
                                index == categories.size - 1 -> endItemShape()
                                else -> middleItemShape()
                            }

                        ListItem(
                            modifier = Modifier.clip(shape),
                            colors = listItemColors(),
                            headlineContent = { Text(text = category.name, maxLines = 1) },
                            supportingContent = {
                                Text(
                                    text =
                                        "${
                                            state.habitsWithAnalytics.count {
                                                it.habit.categoryId == category.id
                                            }
                                        } " +
                                            stringResource(Res.string.habits)
                                )
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { showEditSheet = true }) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.edit),
                                            contentDescription = "Edit",
                                        )
                                    }

                                    IconButton(
                                        onClick = { showDeleteDialog = true },
                                        enabled = categories.size > 1,
                                    ) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.delete),
                                            contentDescription = "Delete",
                                        )
                                    }

                                    AnimatedVisibility(visible = categories.size > 1) {
                                        Icon(
                                            imageVector =
                                                vectorResource(Res.drawable.drag_indicator),
                                            contentDescription = null,
                                            modifier =
                                                Modifier.padding(horizontal = 8.dp)
                                                    .draggableHandle(),
                                        )
                                    }
                                }
                            },
                        )
                    }

                    if (showDeleteDialog) {
                        GritDialog(onDismissRequest = { showDeleteDialog = false }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.warning),
                                contentDescription = null,
                            )

                            Text(
                                text = stringResource(Res.string.delete),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Text(
                                text = stringResource(Res.string.delete_habit_category),
                                textAlign = TextAlign.Center,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = { showDeleteDialog = false },
                                    shapes =
                                        ButtonShapes(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            pressedShape = MaterialTheme.shapes.small,
                                        ),
                                ) {
                                    Text(stringResource(Res.string.cancel))
                                }

                                TextButton(
                                    onClick = {
                                        onAction(HabitsAction.DeleteHabitCategory(category))
                                        showDeleteDialog = false
                                    },
                                    shapes =
                                        ButtonShapes(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            pressedShape = MaterialTheme.shapes.small,
                                        ),
                                ) {
                                    Text(stringResource(Res.string.delete))
                                }
                            }
                        }
                    }

                    if (showEditSheet) {
                        HabitCategoryUpsertSheet(
                            isEditSheet = true,
                            category = category,
                            onDismiss = { showEditSheet = false },
                            onUpsertCategory = {
                                onAction(HabitsAction.AddHabitCategory(it))
                                showEditSheet = false
                            },
                        )
                    }
                }
            }
        }
    }
}
