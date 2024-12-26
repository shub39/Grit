package com.shub39.grit.habits.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeekActivity(
    dates: List<LocalDate>,
    modifier: Modifier = Modifier
) {
    val currentDate = LocalDate.now()
    val startOfWeek = currentDate.with(DayOfWeek.MONDAY)
    val currentWeekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Box(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(currentWeekDates, key = { it.dayOfWeek.name }) { day ->
                val isHighlighted = dates.contains(day)
                val enabled = day.isBefore(currentDate) || day.isEqual(currentDate)

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (enabled) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        },
                        contentColor = if (enabled) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    ),
                    shape = if (isHighlighted) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = day.dayOfWeek.name.take(3),
                        modifier = Modifier.padding(8.dp),
                        textDecoration = if (isHighlighted) TextDecoration.LineThrough else null
                    )
                }
            }
        }
    }
}