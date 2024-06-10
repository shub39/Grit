package com.shub39.grit.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.component.EmptyPage
import com.shub39.grit.component.HabitMap
import com.shub39.grit.viewModel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsPage(
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()
    val habitsStatuses by viewModel.habitStatuses.collectAsState()
    val habitsIsEmpty = habits.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(id = R.string.analytics),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) }
            )
        }
    ) { innerpadding ->

        if (habitsIsEmpty) {
            EmptyPage(paddingValues = innerpadding)
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerpadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(habits, key = { it.id }) { habit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    ) {
                        Text(
                            text = habit.id,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HabitMap(data = habitsStatuses.filter { it.id == habit.id })
                    }
                }
            }
        }
    }
}