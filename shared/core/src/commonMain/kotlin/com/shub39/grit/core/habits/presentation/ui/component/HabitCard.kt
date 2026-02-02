package com.shub39.grit.core.habits.presentation.ui.component

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.utils.now
import com.shub39.grit.core.utils.toFormattedString
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.analytics
import grit.shared.core.generated.resources.check_circle
import grit.shared.core.generated.resources.circle_border
import grit.shared.core.generated.resources.heat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HabitCard(
    habitWithAnalytics: HabitWithAnalytics,
    completed: Boolean,
    action: (HabitsAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    editState: Boolean,
    compactView: Boolean,
    analyticsEnabled: Boolean,
    startingDay: DayOfWeek,
    reorderHandle: @Composable () -> Unit,
    is24Hr: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val canCompleteToday = today.dayOfWeek in habitWithAnalytics.habit.days

    // animated colors
    val cardContent by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface.copy(
                alpha = if (canCompleteToday) 1f else 0.7f
            )
        },
        label = "cardBackground"
    )
    val cardBackground by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer.copy(
                alpha = if (canCompleteToday) 1f else 0.7f
            )
        },
        label = "cardBackground"
    )

    val weekState = rememberWeekCalendarState(
        startDate = habitWithAnalytics.habit.time.date.minus(1, DateTimeUnit.YEAR),
        endDate = today,
        firstVisibleWeekDate = today,
        firstDayOfWeek = startingDay
    )

    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = cardBackground,
            contentColor = cardContent
        ),
        onClick = {
            if (canCompleteToday) {
                action(HabitsAction.InsertStatus(habitWithAnalytics.habit, today))
            }
        },
        shape = shape,
        modifier = modifier.animateContentSize()
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large),
            colors = ListItemDefaults.colors(
                containerColor = cardBackground,
                headlineColor = cardContent,
                supportingColor = cardContent,
                trailingIconColor = cardContent,
                leadingIconColor = cardContent
            ),
            leadingContent = {
                AnimatedContent(
                    targetState = completed
                ) {
                    Icon(
                        imageVector = vectorResource(if (!it) Res.drawable.circle_border else Res.drawable.check_circle),
                        contentDescription = null,
                    )
                }
            },
            headlineContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = habitWithAnalytics.habit.title,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.basicMarquee()
                    )
                }
            },
            supportingContent = {
                if (habitWithAnalytics.habit.reminder) {
                    Text(
                        text = habitWithAnalytics.habit.time.time.toFormattedString(is24Hr)
                    )
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heat),
                            contentDescription = null
                        )

                        Text(
                            text = habitWithAnalytics.currentStreak.toString()
                        )
                    }

                    IconButton(
                        onClick = {
                            action(HabitsAction.PrepareAnalytics(habitWithAnalytics.habit))
                            onNavigateToAnalytics()
                        },
                        enabled = analyticsEnabled
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.analytics),
                            contentDescription = "Analytics"
                        )
                    }

                    AnimatedVisibility(
                        visible = editState
                    ) {
                        reorderHandle()
                    }
                }
            }
        )

        if (!compactView) {
            WeekCalendar(
                contentPadding = PaddingValues(8.dp),
                state = weekState,
                dayContent = { weekDay ->
                    val done = habitWithAnalytics.statuses.any { it.date == weekDay.date }
                    val validDay =
                        weekDay.date <= today && weekDay.date.dayOfWeek in habitWithAnalytics.habit.days

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(1f)
                            .clickable(
                                role = Role.Button,
                                enabled = validDay,
                                onClick = {
                                    action(
                                        HabitsAction.InsertStatus(
                                            habit = habitWithAnalytics.habit,
                                            date = weekDay.date
                                        )
                                    )
                                }
                            )
                            .then(
                                if (done) {
                                    val donePrevious =
                                        habitWithAnalytics.statuses.any {
                                            it.date == weekDay.date.minusDays(
                                                1
                                            )
                                        }
                                    val doneAfter =
                                        habitWithAnalytics.statuses.any {
                                            it.date == weekDay.date.plusDays(
                                                1
                                            )
                                        }

                                    Modifier.background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = if (donePrevious && doneAfter) {
                                            RoundedCornerShape(10.dp)
                                        } else if (donePrevious) {
                                            RoundedCornerShape(
                                                topStart = 10.dp,
                                                bottomStart = 10.dp,
                                                topEnd = 20.dp,
                                                bottomEnd = 20.dp
                                            )
                                        } else if (doneAfter) {
                                            RoundedCornerShape(
                                                topStart = 20.dp,
                                                bottomStart = 20.dp,
                                                topEnd = 10.dp,
                                                bottomEnd = 10.dp
                                            )
                                        } else {
                                            RoundedCornerShape(20.dp)
                                        }
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = weekDay.date.day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (done) MaterialTheme.colorScheme.onPrimary
                                else if (!validDay) cardContent.copy(alpha = 0.5f)
                                else cardContent
                            )

                            Text(
                                text = weekDay.date.dayOfWeek.toString().take(3),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (done) MaterialTheme.colorScheme.onPrimary
                                else if (!validDay) cardContent.copy(alpha = 0.5f)
                                else cardContent
                            )
                        }
                    }
                }
            )
        }
    }
}