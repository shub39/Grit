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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitMap
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.ui.theme.Typography
import com.shub39.grit.viewModel.HabitViewModel

@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()
    val habitsIsEmpty = habits.isEmpty()

    Scaffold { innerpadding ->

        if (habitsIsEmpty) {
            EmptyPage(paddingValues = innerpadding)
        }

        var expandedCardId by remember { mutableStateOf<String?>(null) }

        LazyColumn(
            modifier = Modifier
                .padding(innerpadding)
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
    val doneDays = viewModel.getStatusForHabit(habit.id).size.toString()
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
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = doneDays +" "+ stringResource(R.string.days_completed),
                    style = Typography.bodyLarge,
                )
            }
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