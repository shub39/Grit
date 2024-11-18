package com.shub39.grit.ui.page.task_page.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shub39.grit.R

// needs work, looks jarring currently
@Composable
fun TasksGuide() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.tasks_guide_1))
        Text(stringResource(R.string.tasks_guide_2))
    }
}