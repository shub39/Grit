package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme

@Composable
fun HabitStreakCard(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondary,
            leadingIconColor = MaterialTheme.colorScheme.onSecondary,
            headlineColor = MaterialTheme.colorScheme.onSecondary,
            overlineColor = MaterialTheme.colorScheme.onSecondary,
            supportingColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp,
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp
                )
            ),
        leadingContent = {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = "Streak",
                modifier = Modifier.size(64.dp)
            )
        },
        overlineContent = {
            Text(
                text = stringResource(R.string.streak),
            )
        },
        headlineContent = {
            Text(
                text = currentStreak.toString(),
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.best_streak, bestStreak),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        HabitStreakCard(
            currentStreak = 10,
            bestStreak = 100
        )
    }
}