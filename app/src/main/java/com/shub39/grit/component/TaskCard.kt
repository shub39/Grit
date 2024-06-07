package com.shub39.grit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.task.Task

@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (Task) -> Unit,
) {
    var taskStatus by remember { mutableStateOf(task.status) }

    val modifier = {
        if (taskStatus) {
            Modifier.blur(5.dp)
        } else {
            Modifier.blur(0.dp)
        }
    }

    Card(
        colors = getCardColors(task.priority),
        modifier = modifier()
            .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp)
                .clickable {
                    taskStatus = !taskStatus
                    task.status = !task.status
                    onStatusChange(task)
                }
        ) {
            Text(
                text = task.title,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getCardColors(priority: Boolean): CardColors {
    return when (priority) {
        true -> CardColors(
            containerColor = Color.Red,
            contentColor = Color.Red,
            disabledContentColor = Color.Red,
            disabledContainerColor = Color.Red
        )

        else -> CardColors(
            containerColor = Color.Yellow,
            contentColor = Color.Yellow,
            disabledContentColor = Color.Yellow,
            disabledContainerColor = Color.Yellow
        )
    }
}