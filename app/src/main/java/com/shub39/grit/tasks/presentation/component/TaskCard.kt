package com.shub39.grit.tasks.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.domain.TaskPriority
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskCard(
    task: Task,
    dragState: Boolean = false,
    reorderIcon: @Composable () -> Unit,
    shape: Shape,
    modifier: Modifier
) {
    val cardContent by animateColorAsState(
        targetValue = when (task.status) {
            true -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.secondary
        },
        label = "cardContent"
    )
    val cardContainer by animateColorAsState(
        targetValue = when (task.status) {
            true -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "cardContainer"
    )
    val cardColors = CardDefaults.cardColors(
        containerColor = cardContainer,
        contentColor = cardContent
    )

    Card(
        modifier = modifier,
        colors = cardColors,
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                // Priority indicator and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Priority indicator
                    if (task.priority != TaskPriority.MEDIUM) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when (task.priority) {
                                    TaskPriority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                    TaskPriority.LOW -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = when (task.priority) {
                                    TaskPriority.HIGH -> "High"
                                    TaskPriority.LOW -> "Low"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (task.priority) {
                                    TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                                    TaskPriority.LOW -> MaterialTheme.colorScheme.primary
                                    else -> Color.Unspecified
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.status) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                }
                
                // Deadline if present
                task.deadline?.let { deadline ->
                    val isOverdue = deadline.isBefore(LocalDateTime.now()) && !task.status
                    Text(
                        text = "Due: ${deadline.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))}",
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            isOverdue -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = dragState,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                reorderIcon()
            }
        }
    }
}