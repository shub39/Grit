package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.LocalFireDepartment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.HabitsPageAction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habit: Habit,
    statusList: List<HabitStatus>,
    completed: Boolean,
    action: (HabitsPageAction) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    editState: Boolean,
    startingDay: DayOfWeek,
    reorderHandle: @Composable () -> Unit,
    timeFormat: String,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    // animated colors
    val cardContent by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "cardBackground"
    )
    val cardBackground by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        label = "cardBackground"
    )

    val weekState = rememberWeekCalendarState(
        startDate = habit.time.toLocalDate().minusMonths(12),
        endDate = LocalDate.now(),
        firstVisibleWeekDate = LocalDate.now(),
        firstDayOfWeek = startingDay
    )

    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = cardBackground,
            contentColor = cardContent
        ),
        onClick = { action(HabitsPageAction.InsertStatus(habit)) },
        shape = shape,
        modifier = modifier
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
                        imageVector = if (!it) Icons.Outlined.Circle else Icons.Rounded.CheckCircleOutline,
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
                        text = habit.title,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.basicMarquee()
                    )
                }
            },
            supportingContent = {
                Text(
                    text = habit.time.format(DateTimeFormatter.ofPattern(timeFormat))
                )
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
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null
                        )

                        Text(
                            text = countCurrentStreak(statusList.map { it.date }).toString()
                        )
                    }

                    IconButton(
                        onClick = {
                            action(HabitsPageAction.PrepareAnalytics(habit))
                            onNavigateToAnalytics()
                        },
                        enabled = !editState
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Analytics,
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

        WeekCalendar(
            modifier = Modifier.padding(8.dp),
            state = weekState,
            dayContent = { weekDay ->
                val done = statusList.any { it.date == weekDay.date }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .then(
                            if (done) {
                                val donePrevious =
                                    statusList.any { it.date == weekDay.date.minusDays(1) }
                                val doneAfter =
                                    statusList.any { it.date == weekDay.date.plusDays(1) }

                                Modifier.background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape =  if (donePrevious && doneAfter) {
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
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = weekDay.date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (done) MaterialTheme.colorScheme.onPrimary else cardContent
                        )

                        Text(
                            text = weekDay.date.dayOfWeek.toString().take(3),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (done) MaterialTheme.colorScheme.onPrimary else cardContent
                        )
                    }
                }
            }
        )
    }
}