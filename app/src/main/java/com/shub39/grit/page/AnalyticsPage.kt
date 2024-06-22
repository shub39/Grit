package com.shub39.grit.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel,
    onClick: () -> Unit
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
            item {
                ElevatedCard(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp),
                    onClick = {
                        onClick()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.made_by) + " shub39",
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
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
    var doneDays by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }

    LaunchedEffect(doneDays, streak, bestStreak) {
        doneDays = viewModel.getStatusForHabit(habit.id).size
        streak = viewModel.getStreakForHabit(habit.id)
        bestStreak = viewModel.getBestStreakForHabit(habit.id)
    }

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
            Text(
                text = habit.description,
                style = Typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    HabitAssistChip(
                        text = stringResource(id = R.string.streak) + " " + streak.toString(),
                        iconId = R.drawable.round_local_fire_department_24,
                        onClick = {}
                    )
                }
                item {
                    HabitAssistChip(
                        text = stringResource(id = R.string.best_streak) + " " + bestStreak.toString(),
                        iconId = R.drawable.round_local_fire_department_24,
                        onClick = {}
                    )
                }
                item {
                    HabitAssistChip(
                        text = stringResource(id = R.string.days_completed) + " " + doneDays.toString(),
                        iconId = R.drawable.round_checklist_24,
                        onClick = {}
                    )
                }
                item {
                    HabitAssistChip(
                        text = habit.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        iconId = R.drawable.round_calendar_month_24,
                        onClick = {}
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
fun HabitAssistChip(
    text: String,
    iconId: Int,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = text) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        }
    )
}