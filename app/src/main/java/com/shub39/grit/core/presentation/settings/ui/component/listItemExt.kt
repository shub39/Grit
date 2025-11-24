package com.shub39.grit.core.presentation.settings.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private const val connectedCornerRadius = 8
private const val endCornerRadius = 16

@Composable
fun listItemColors(): ListItemColors {
    return ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

fun leadingItemShape(): Shape = RoundedCornerShape(
    topStart = endCornerRadius.dp,
    topEnd = endCornerRadius.dp,
    bottomEnd = connectedCornerRadius.dp,
    bottomStart = connectedCornerRadius.dp
)

fun middleItemShape(): Shape = RoundedCornerShape(
    topStart = connectedCornerRadius.dp,
    topEnd = connectedCornerRadius.dp,
    bottomStart = connectedCornerRadius.dp,
    bottomEnd = connectedCornerRadius.dp
)

fun endItemShape(): Shape = RoundedCornerShape(
    topStart = connectedCornerRadius.dp,
    topEnd = connectedCornerRadius.dp,
    bottomEnd = endCornerRadius.dp,
    bottomStart = endCornerRadius.dp
)