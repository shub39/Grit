package com.shub39.grit.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.ui.theme.Typography
import com.shub39.grit.viewModel.HabitViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HabitAnalyticsCard(
    habit: Habit,
    viewModel: HabitViewModel = koinViewModel(),
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
            .padding(top = 4.dp, bottom = 4.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(true, radius = Dp.Infinity),
                onClick = { onItemClick() }
            ),
        shape = MaterialTheme.shapes.extraLarge
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
                HabitMap(habit = habit)
            }
        }
    }
}