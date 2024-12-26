package com.shub39.grit.habits.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.GritDialog
import com.shub39.grit.core.presentation.GritTheme
import com.shub39.grit.core.presentation.localToTimePickerState
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.AnalyticsSheet
import com.shub39.grit.habits.presentation.HabitsPageAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habit: Habit,
    statusList: List<HabitStatus>,
    completed: Boolean,
    action: (HabitsPageAction) -> Unit,
) {
    // controllers
    var showEditDialog by remember { mutableStateOf(false) }
    var showAnalyticsSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

    OutlinedCard(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = CardDefaults.outlinedCardBorder(
            enabled = completed
        )
    ) {
        ListItem(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .combinedClickable(
                    onClick = { action(HabitsPageAction.InsertStatus(habit)) },
                    onLongClick = { showEditDialog = true },
                    onDoubleClick = { showAnalyticsSheet = true }
                ),
            colors = ListItemDefaults.colors(
                containerColor = cardBackground,
                headlineColor = cardContent,
                trailingIconColor = cardContent
            ),
            leadingContent = {
                AnimatedVisibility(
                    visible = completed,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    Icon(
                        painterResource(R.drawable.round_check_circle_24),
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            supportingContent = {
                Text(
                    text = habit.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_alarm_24),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = habit.time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    )
                }
            }
        )

        WeekActivity(
            dates = statusList.map { it.date },
            modifier = Modifier.padding(8.dp)
        )
    }

    // delete dialog
    if (showDeleteDialog) {
        GritDialog(
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_warning_24),
                contentDescription = null,
            )

            Text(
                text = stringResource(R.string.delete),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.delete_warning),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    showDeleteDialog = false
                    action(HabitsPageAction.DeleteHabit(habit))
                }
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }

    if (showAnalyticsSheet) {
        AnalyticsSheet(
            habit = habit,
            statusList = statusList,
            onDismiss = { showAnalyticsSheet = false },
            action = action
        )
    }

    if (showEditDialog) {
        var newHabitTitle by remember { mutableStateOf(habit.title) }
        var newHabitDescription by remember { mutableStateOf(habit.description) }
        val timePickerState = remember { localToTimePickerState(habit.time) }

        GritDialog(
            onDismissRequest = { showEditDialog = false }
        ) {
            OutlinedTextField(
                value = newHabitTitle,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitTitle = it },
                label = {
                    if (newHabitTitle.length <= 20) {
                        Text(text = stringResource(id = R.string.update_title))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitDescription.length > 20
            )

            OutlinedTextField(
                value = newHabitDescription,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newHabitDescription = it },
                label = {
                    if (newHabitDescription.length <= 50) {
                        Text(text = stringResource(id = R.string.update_description))
                    } else {
                        Text(text = stringResource(id = R.string.too_long))
                    }
                },
                isError = newHabitDescription.length > 50
            )

            TimePicker(
                state = timePickerState
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        showEditDialog = false
                        showDeleteDialog = true
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = {
                        showEditDialog = false
                        action(
                            HabitsPageAction.UpdateHabit(
                                Habit(
                                    id = habit.id,
                                    title = newHabitTitle,
                                    description = newHabitDescription,
                                    time = habit.time.withHour(timePickerState.hour)
                                        .withMinute(timePickerState.minute)
                                )
                            )
                        )
                    },
                    enabled = newHabitDescription.isNotBlank() && newHabitDescription.length <= 50 && newHabitTitle.length <= 20,
                ) {
                    Text(text = stringResource(id = R.string.update))
                }
            }
        }
    }
}

@Preview
@Composable
private fun HabitCardPreview() {
    GritTheme {
        Column {
            HabitCard(
                habit = Habit(
                    id = 1,
                    title = "A Habit",
                    description = "Description for Habit",
                    time = LocalDateTime.now()
                ),
                statusList = listOf(),
                completed = true
            ) { }

            Spacer(modifier = Modifier.height(16.dp))

            HabitCard(
                habit = Habit(
                    id = 1,
                    title = "A Habit",
                    description = "Description for Habit",
                    time = LocalDateTime.now()
                ),
                statusList = listOf(),
                completed = false
            ) { }
        }
    }
}