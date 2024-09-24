package com.shub39.grit.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.task.Task
import com.shub39.grit.logic.UILogic.getCardColors

@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (Task) -> Unit,
) {
    var taskStatus by remember { mutableStateOf(task.status) }

    val lineThrough = {
        if (taskStatus) {
            TextDecoration.LineThrough
        } else {
            TextDecoration.None
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp), onClick = {
            taskStatus = !taskStatus
            task.status = !task.status
            onStatusChange(task)
        }, colors = getCardColors(task.priority), shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = task.title,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textDecoration = lineThrough(),
        )
    }
}