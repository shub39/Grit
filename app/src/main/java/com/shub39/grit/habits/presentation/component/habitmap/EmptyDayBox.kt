package com.shub39.grit.habits.presentation.component.habitmap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// empty placeholder box for end of the month
@Composable
fun EmptyDayBox() {
    Box(
        modifier = Modifier
            .size(30.dp)
    )
}