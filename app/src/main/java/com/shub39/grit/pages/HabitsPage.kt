package com.shub39.grit.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.components.HabitCard
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.viewModel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPage(habitViewModel: HabitViewModel) {
    val habits by habitViewModel.habits.collectAsState()
    val habitListIsEmpty = habits.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.habits),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    habitViewModel.addHabit(Habit("New Habit", "New Habit", "This is a test habit", false))
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_add_24),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = stringResource(id = R.string.add_habit))
                }
            }
        }
    ) {paddingValues->
        if (habitListIsEmpty) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.empty),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            HabitsList(
                viewModel = habitViewModel,
                paddingValue = paddingValues
            )
        }
    }
}

@Composable
private fun HabitsList(
    viewModel: HabitViewModel,
    paddingValue: PaddingValues
) {
    val habits by viewModel.habits.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(paddingValue),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(habits, key = { it.id }) {
            HabitCard(habit = it)
        }
    }
}