package com.shub39.grit.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun HabitAssistChip(
    text: String,
    iconId: Int,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = text) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}