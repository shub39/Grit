package com.shub39.grit.ui.page

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
import com.shub39.grit.ui.component.EmptyPage
import com.shub39.grit.ui.component.HabitAnalyticsCard
import com.shub39.grit.viewModel.HabitViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel = koinViewModel(),
) {
    val habits by viewModel.habits.collectAsState()
    val habitsIsEmpty = habits.isEmpty()

    Scaffold { innerPadding ->

        if (habitsIsEmpty) {
            EmptyPage()
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
                    isLastRowVisible = expandedCardId == habit.id,
                    onItemClick = {
                        expandedCardId = if (expandedCardId == habit.id) null else habit.id
                    }
                )
            }
        }
    }
}