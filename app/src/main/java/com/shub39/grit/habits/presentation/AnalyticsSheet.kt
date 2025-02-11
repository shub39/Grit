package com.shub39.grit.habits.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.shub39.grit.core.presentation.countBestStreak
import com.shub39.grit.core.presentation.countCurrentStreak
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.presentation.component.AnalyticsCard
import com.shub39.grit.habits.presentation.component.BooleanHeatMap
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheet(
    habit: Habit,
    statusList: List<HabitStatus>,
    onDismiss: () -> Unit,
    staringDay: DayOfWeek = DayOfWeek.MONDAY,
    action: (HabitsPageAction) -> Unit
) {
    var updateTrigger by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))
            }

            item {
                AnalyticsCard(title = stringResource(R.string.habit_map)) {
                    BooleanHeatMap(
                        dates = statusList.map { it.date },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        editEnabled = true,
                        onClick = { date ->
                            action(HabitsPageAction.InsertStatus(habit, date))
                            updateTrigger = !updateTrigger
                        },
                        startFrom = staringDay
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))
            }

            item {
                AnalyticsCard(title = stringResource(R.string.habit_stats)) {
                    Row(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(6.dp)
                                .height(150.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_local_fire_department_24),
                                    contentDescription = "Streak"
                                )

                                Text(
                                    text = countCurrentStreak(statusList.map { it.date }).toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = stringResource(R.string.streak),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .padding(6.dp)
                                .height(150.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_local_fire_department_24),
                                    contentDescription = "Streak",
                                )

                                Text(
                                    text = countBestStreak(statusList.map { it.date }).toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = stringResource(R.string.best_streak),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .padding(6.dp)
                                .height(150.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_flag_circle_24),
                                    contentDescription = "Flag"
                                )

                                Text(
                                    text = habit.time.format(DateTimeFormatter.ofPattern("dd")),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = habit.time.format(DateTimeFormatter.ofPattern("MMM\nu")),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}