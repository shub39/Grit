package com.shub39.grit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.database.habit.Habit

@Composable
fun HabitCard(
    habit: Habit,
    onHabitClick: (Habit) -> Unit = {},
) {
    var buttonState by remember { mutableStateOf(false) }

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = {
                    onHabitClick(habit)
                    buttonState = buttonState.not()
                },
                modifier = Modifier.align(Alignment.CenterVertically),
                enabled = !buttonState
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_check_24),
                    tint = MaterialTheme.colorScheme.surface,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
@Preview
fun HabitCardPreview() {
    HabitCard(habit = Habit)
}

val Habit = Habit("excercise", "Daily Excercise", "Daily Workout atleast 20 minutes", false)