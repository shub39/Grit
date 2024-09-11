package com.shub39.grit.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitAnalyticsCard
import com.shub39.grit.viewModel.HabitViewModel

@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel,
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
                HabitAnalyticsCard(
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