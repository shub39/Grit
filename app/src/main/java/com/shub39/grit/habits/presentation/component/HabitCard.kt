package com.shub39.grit.habits.presentation.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.HabitsPageAction
import java.time.DayOfWeek
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
    modifier: Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    // animated colors
    val cardContent by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondary
        },
        label = "cardContent"
    )
    val cardBackground by animateColorAsState(
        targetValue = when (completed) {
            true -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "cardBackground"
    )

    val weekState = rememberWeekCalendarState(
        startDate = habit.time.toLocalDate().minusMonths(100),
        endDate = habit.time.toLocalDate(),
        firstVisibleWeekDate = habit.time.toLocalDate(),
        firstDayOfWeek = startingDay
    )

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(
            containerColor = cardBackground.copy(alpha = 0.7f),
            contentColor = cardContent
        ),
        onClick = {
            action(HabitsPageAction.InsertStatus(habit))
        },
        modifier = modifier
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large),
            colors = ListItemDefaults.colors(
                containerColor = cardBackground,
                headlineColor = cardContent,
            ),
            leadingContent = {
                AnimatedContent(
                    targetState = completed
                ) {
                    Icon(
                        painter = painterResource(
                            if (it) R.drawable.round_check_circle_24 else R.drawable.outline_circle_24
                        ),
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
                        modifier = Modifier.basicMarquee()
                    )
                }
            },
            supportingContent = {
                Text(
                    text = habit.time.format(DateTimeFormatter.ofPattern("hh:mm a"))
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
                            painter = painterResource(R.drawable.round_local_fire_department_24),
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
                            painter = painterResource(R.drawable.round_analytics_24),
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
                            if (done) Modifier.background(
                                color = primary.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.large
                            ) else Modifier
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
                            color = if (done) primary else MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = weekDay.date.dayOfWeek.toString().take(3),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (done) primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        )
    }
}