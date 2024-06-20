package com.shub39.grit.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitMap
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.ui.theme.Typography
import com.shub39.grit.viewModel.HabitViewModel
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.min

@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()
    val habitsIsEmpty = habits.isEmpty()

    Scaffold { innerPadding ->

        if (habitsIsEmpty) {
            EmptyPage(paddingValues = innerPadding)
        }

        var expandedCardId by remember { mutableStateOf<String?>(null) }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(habits, key = { it.id }) { habit ->
                HabitCard(
                    habit = habit,
                    viewModel = viewModel,
                    isLastRowVisible = expandedCardId == habit.id,
                    onItemClick = {
                        expandedCardId = if (expandedCardId == habit.id) null else habit.id
                    }
                )
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    viewModel: HabitViewModel,
    isLastRowVisible: Boolean,
    onItemClick: () -> Unit,
) {
    val doneDays = viewModel.getStatusForHabit(habit.id).size
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .clickable { onItemClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            Text(
                text = habit.id,
                style = Typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedCard {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        CircularProgressWithText(
                            habit = habit,
                            days = doneDays
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.days_completed),
                                style = Typography.bodyMedium
                            )
                            Text(
                                text = stringResource(id = R.string.since) + String.format(Locale.getDefault(), " %s %s", habit.time.month, habit.time.year),
                                style = Typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Streak(
                    viewModel.getStreakForHabit(habit.id)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        AnimatedVisibility(
            visible = isLastRowVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                HabitMap(data = viewModel.getStatusForHabit(habit.id))
            }
        }
    }
}


@Composable
private fun CircularProgressWithText(habit: Habit, days: Int) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(habit, days) {
        val day = LocalDateTime.now().dayOfYear - habit.time.dayOfYear
        progress = min(days.toFloat() / day.toFloat(), 1f)
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(64.dp),
        )
        Text(
            text = String.format(Locale.getDefault(), "%.0f%%", progress * 100),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun Streak(number: Int) {
    OutlinedCard {
        Row(
            modifier = Modifier.padding(end = 12.dp, start = 6.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = R.drawable.round_local_fire_department_24),
                contentDescription = null
            )
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}