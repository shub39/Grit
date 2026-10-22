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
package com.shub39.grit.shared.ui.habit.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitWithAnalytics
import com.shub39.grit.core.now
import com.shub39.grit.core.toFormattedString
import grit.shared.ui.generated.resources.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.vectorResource

/** Habit Card for list */
@Composable
fun HabitCard(
    habitWithAnalytics: HabitWithAnalytics,
    completed: Boolean,
    onInsertStatus: (Habit, LocalDate, Double) -> Unit,
    onPrepareAnalytics: (Habit) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    editState: Boolean,
    compactView: Boolean,
    startingDay: DayOfWeek,
    reorderHandle: @Composable () -> Unit,
    is24Hr: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val canCompleteToday = today.dayOfWeek in habitWithAnalytics.habit.days

    // animated colors
    val cardContent by
        animateColorAsState(
            targetValue =
                when (completed) {
                    true -> colorScheme.onPrimaryContainer
                    else -> colorScheme.onSurface.copy(alpha = if (canCompleteToday) 1f else 0.7f)
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardBackground",
        )
    val cardBackground by
        animateColorAsState(
            targetValue =
                when (completed) {
                    true -> colorScheme.primaryContainer
                    else ->
                        colorScheme.surfaceContainer.copy(
                            alpha = if (canCompleteToday) 1f else 0.7f
                        )
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardBackground",
        )

    val weekState =
        rememberWeekCalendarState(
            startDate = habitWithAnalytics.habit.time.date.minus(1, DateTimeUnit.YEAR),
            endDate = today,
            firstVisibleWeekDate = today,
            firstDayOfWeek = startingDay,
        )

    Card(
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = cardBackground,
                contentColor = cardContent,
            ),
        onClick = {
            onInsertStatus(
                habitWithAnalytics.habit,
                today,
                habitWithAnalytics.statuses.find { it.date == today }?.value ?: 0.0,
            )
        },
        enabled = canCompleteToday,
        shape = shape,
        modifier = modifier.animateContentSize(),
    ) {
        ListItem(
            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large),
            colors =
                ListItemDefaults.colors(
                    containerColor = cardBackground,
                    headlineColor = cardContent,
                    supportingColor = cardContent,
                    trailingIconColor = cardContent,
                    leadingIconColor = cardContent,
                ),
            leadingContent = {
                AnimatedContent(targetState = completed) {
                    Icon(
                        imageVector =
                            vectorResource(
                                if (!it) Res.drawable.circle_border else Res.drawable.check_circle
                            ),
                        contentDescription = null,
                    )
                }
            },
            headlineContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = habitWithAnalytics.habit.title,
                        maxLines = 1,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.basicMarquee(),
                    )
                }
            },
            supportingContent = {
                if (habitWithAnalytics.habit.reminder) {
                    Text(
                        text = habitWithAnalytics.habit.time.time.toFormattedString(is24Hr),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heat),
                            contentDescription = null,
                        )

                        Text(text = habitWithAnalytics.currentStreak.toString())
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            onPrepareAnalytics(habitWithAnalytics.habit)
                            onNavigateToAnalytics()
                        },
                        modifier =
                            Modifier.size(
                                IconButtonDefaults.smallContainerSize(
                                    IconButtonDefaults.IconButtonWidthOption.Wide
                                )
                            ),
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary,
                            ),
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.analytics),
                            contentDescription = "Analytics",
                        )
                    }

                    AnimatedVisibility(visible = editState) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            reorderHandle()
                        }
                    }
                }
            },
        )

        if (!compactView) {
            WeekCalendar(
                contentPadding = PaddingValues(8.dp),
                state = weekState,
                dayContent = { weekDay ->
                    val done = habitWithAnalytics.statuses.find { it.date == weekDay.date }
                    val validDay =
                        weekDay.date <= today &&
                            weekDay.date.dayOfWeek in habitWithAnalytics.habit.days

                    val backgroundColor =
                        if (done != null) {
                            colorScheme.primary.copy(
                                alpha =
                                    if (habitWithAnalytics.habit.isMeasurable) {
                                        (done.value / habitWithAnalytics.habit.targetValue)
                                            .toFloat()
                                            .coerceIn(0f, 1f)
                                    } else 1f
                            )
                        } else colorScheme.surface
                    val contentColor =
                        if (done != null) {
                            if (!habitWithAnalytics.habit.isMeasurable) colorScheme.onPrimary
                            else {
                                val progress =
                                    (done.value / habitWithAnalytics.habit.targetValue)
                                        .toFloat()
                                        .coerceIn(0f, 1f)
                                if (progress >= 0.7f) colorScheme.onPrimary
                                else colorScheme.onSurface
                            }
                        } else if (!validDay) cardContent.copy(alpha = 0.5f) else cardContent

                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .then(
                                    if (done != null) {
                                        val donePrevious =
                                            habitWithAnalytics.statuses.any {
                                                it.date == weekDay.date.minusDays(1)
                                            }
                                        val doneAfter =
                                            habitWithAnalytics.statuses.any {
                                                it.date == weekDay.date.plusDays(1)
                                            }
                                        val shape =
                                            when {
                                                donePrevious && doneAfter ->
                                                    RoundedCornerShape(0.dp)

                                                donePrevious ->
                                                    RoundedCornerShape(
                                                        topEnd = 20.dp,
                                                        bottomEnd = 20.dp,
                                                    )

                                                doneAfter ->
                                                    RoundedCornerShape(
                                                        topStart = 20.dp,
                                                        bottomStart = 20.dp,
                                                    )

                                                else -> RoundedCornerShape(20.dp)
                                            }

                                        Modifier.background(color = backgroundColor, shape = shape)
                                    } else Modifier
                                )
                                .clip(shape = RoundedCornerShape(20.dp))
                                .clickable(
                                    role = Role.Button,
                                    enabled = validDay,
                                    onClick = {
                                        onInsertStatus(
                                            habitWithAnalytics.habit,
                                            weekDay.date,
                                            done?.value ?: 0.0,
                                        )
                                    },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = weekDay.date.day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                                color = contentColor,
                            )

                            Text(
                                text = weekDay.date.dayOfWeek.toString().take(3),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                                color = contentColor,
                            )
                        }
                    }
                },
            )
        }
    }
}
