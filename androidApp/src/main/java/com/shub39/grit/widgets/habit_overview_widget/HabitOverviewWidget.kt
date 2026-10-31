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
package com.shub39.grit.widgets.habit_overview_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitCategory
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.habits.HabitStatusHandler
import com.shub39.grit.core.habits.isCompletedToday
import com.shub39.grit.core.now
import com.shub39.grit.widgets.WidgetSize
import com.shub39.grit.widgets.common.ListSlot
import com.shub39.grit.widgets.common.NothingToShow
import com.shub39.grit.widgets.common.SectionHeader
import com.shub39.grit.widgets.common.connectedBackground
import com.shub39.grit.widgets.common.formatValue
import com.shub39.grit.widgets.common.slotOf
import com.shub39.grit.widgets.common.widgetSurface
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/** A group of habits, [title] is null for habits without a category */
private data class HabitSection(val title: String?, val habits: List<Pair<Habit, HabitStatus?>>)

/** Habits scheduled for [today] grouped by their category, categories in their own order */
private fun habitSections(
    habits: List<Pair<Habit, HabitStatus?>>,
    categories: List<HabitCategory>,
    today: LocalDate,
): List<HabitSection> {
    val scheduled = habits.filter { today.dayOfWeek in it.first.days }
    val categoryIds = categories.map { it.id }.toSet()

    val uncategorised =
        scheduled.filter { it.first.categoryId !in categoryIds }.sortedBy { it.first.index }

    val sections =
        categories.mapNotNull { category ->
            val habitsInCategory =
                scheduled.filter { it.first.categoryId == category.id }.sortedBy { it.first.index }

            if (habitsInCategory.isEmpty()) null else HabitSection(category.name, habitsInCategory)
        }

    return if (uncategorised.isEmpty()) sections
    else listOf(HabitSection(null, uncategorised)) + sections
}

class HabitOverviewWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<HabitRepo>()
        val statusHandler = get<HabitStatusHandler>()

        provideContent {
            val size = LocalSize.current
            val scope = rememberCoroutineScope()
            val habitsWithStatus by
                repo.getHabitsWithTodayStatus().collectAsState(initial = emptyList())
            val categories by repo.getHabitCategoriesFlow().collectAsState(initial = emptyList())

            key(size) {
                GlanceTheme {
                    Content(
                        sections = habitSections(habitsWithStatus, categories, LocalDate.now()),
                        onToggleHabit = { habit ->
                            scope.launch {
                                statusHandler.toggle(habit)
                                this@HabitOverviewWidget.update(context, id)
                            }
                        },
                        onUpdateWidget = {
                            scope.launch { this@HabitOverviewWidget.update(context, id) }
                        },
                    )
                }
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            Content(sections = previewSections, onToggleHabit = {}, onUpdateWidget = {})
        }
    }
}

private val previewSections =
    listOf(
        HabitSection(
            title = "Health",
            habits =
                listOf(
                    Habit(
                        id = 1,
                        title = "Read a Book",
                        description = "20 pages at least",
                        time = LocalDateTime.now(),
                        days = emptySet(),
                        index = 1,
                        reminder = false,
                    ) to null,
                    Habit(
                        id = 2,
                        title = "Exercise",
                        description = "40 Minutes daily",
                        time = LocalDateTime.now(),
                        days = emptySet(),
                        index = 2,
                        reminder = false,
                    ) to null,
                ),
        ),
        HabitSection(
            title = null,
            habits =
                listOf(
                    Habit(
                        id = 3,
                        title = "Drink Water",
                        description = "",
                        time = LocalDateTime.now(),
                        days = emptySet(),
                        index = 3,
                        reminder = false,
                        targetValue = 10.0,
                        targetUnit = "glasses",
                    ) to HabitStatus(id = 1, habitId = 3, date = LocalDate.now(), value = 6.0)
                ),
        ),
    )

@Composable
@GlanceComposable
private fun Content(
    sections: List<HabitSection>,
    onToggleHabit: (Habit) -> Unit,
    onUpdateWidget: () -> Unit,
    modifier: GlanceModifier = GlanceModifier,
) {
    val size = LocalSize.current
    val isWide = size.width >= WidgetSize.Width4
    val allHabits = sections.flatMap { it.habits }
    val completedCount = allHabits.count { it.first.isCompletedToday(it.second) }

    Column(
        modifier =
            modifier.fillMaxSize().widgetSurface().clickable(actionStartActivity<MainActivity>())
    ) {
        TitleBar(
            startIcon = ImageProvider(R.drawable.alarm),
            title = "Habits",
            actions = {
                Text(
                    text = "$completedCount/${allHabits.size}",
                    style = TextStyle(color = GlanceTheme.colors.onSurface),
                )

                if (isWide) {
                    Box(GlanceModifier.padding(horizontal = 16.dp)) {
                        Image(
                            provider = ImageProvider(R.drawable.refresh),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                            modifier = GlanceModifier.clickable { onUpdateWidget() },
                        )
                    }
                } else {
                    Spacer(modifier = GlanceModifier.width(16.dp))
                }
            },
        )

        if (allHabits.isEmpty()) {
            NothingToShow()
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                sections.forEach { section ->
                    val title = section.title

                    if (title != null) {
                        item {
                            SectionHeader(
                                text = title,
                                modifier =
                                    GlanceModifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }

                    items(
                        section.habits.mapIndexed { index, item -> index to item },
                        itemId = { it.second.first.id },
                    ) { (index, habitWithStatus) ->
                        // 2dp between items, matching the habits list in the app
                        Column(modifier = GlanceModifier.padding(bottom = 2.dp)) {
                            HabitItem(
                                habit = habitWithStatus.first,
                                status = habitWithStatus.second,
                                slot = slotOf(index, section.habits.size),
                                isWide = isWide,
                                onToggleHabit = onToggleHabit,
                            )
                        }
                    }

                    item { Spacer(modifier = GlanceModifier.height(12.dp)) }
                }

                item { Spacer(modifier = GlanceModifier.height(4.dp)) }
            }
        }
    }
}

@Composable
@GlanceComposable
private fun HabitItem(
    habit: Habit,
    status: HabitStatus?,
    slot: ListSlot,
    isWide: Boolean,
    onToggleHabit: (Habit) -> Unit,
) {
    val completed = habit.isCompletedToday(status)
    val containerColor =
        if (completed) GlanceTheme.colors.tertiaryContainer
        else GlanceTheme.colors.secondaryContainer
    val contentColor =
        if (completed) GlanceTheme.colors.onTertiaryContainer
        else GlanceTheme.colors.onSecondaryContainer
    val textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None

    Row(
        modifier =
            GlanceModifier.fillMaxWidth()
                .connectedBackground(slot, containerColor)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clickable { onToggleHabit(habit) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isWide) {
            Image(
                provider =
                    ImageProvider(
                        if (completed) R.drawable.check_circle else R.drawable.circle_border
                    ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(contentColor),
                modifier = GlanceModifier.size(16.dp),
            )

            Spacer(modifier = GlanceModifier.width(8.dp))
        }

        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = habit.title,
                maxLines = 1,
                style =
                    TextStyle(
                        color = contentColor,
                        textDecoration = textDecoration,
                        fontWeight = FontWeight.Bold,
                    ),
            )

            if (habit.description.isNotEmpty()) {
                Text(
                    text = habit.description,
                    maxLines = 1,
                    style = TextStyle(color = contentColor, textDecoration = textDecoration),
                )
            }
        }

        if (isWide && habit.isMeasurable) {
            Text(
                text =
                    "${formatValue(status?.value ?: 0.0)}/${formatValue(habit.targetValue)}" +
                        if (habit.targetUnit.isBlank()) "" else " ${habit.targetUnit}",
                maxLines = 1,
                style = TextStyle(color = contentColor, fontSize = 10.sp),
                modifier = GlanceModifier.padding(start = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun GlancePreview() {
    Content(sections = previewSections, onToggleHabit = {}, onUpdateWidget = {})
}
