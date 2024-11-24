package com.shub39.grit.habits.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shub39.grit.R

// needs work
@Composable
fun HabitGuide() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.habits_guide_1))
        Text(stringResource(R.string.habits_guide_2))
        Text(stringResource(R.string.habits_guide_3))
    }
}