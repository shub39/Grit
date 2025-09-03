package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlagCircle
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
import com.shub39.grit.habits.presentation.formatDateWithOrdinal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun HabitStartCard(
    today: LocalDate,
    habitDate: LocalDateTime,
    modifier: Modifier = Modifier
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
            overlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
            supportingColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            ),
        leadingContent = {
            Icon(
                imageVector = Icons.Rounded.FlagCircle,
                contentDescription = "Flag",
                modifier = Modifier.size(64.dp)
            )
        },
        overlineContent = {
            Text(
                text = stringResource(R.string.started_on),
            )
        },
        headlineContent = {
            Text(
                text = formatDateWithOrdinal(habitDate.toLocalDate()),
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(
                text = stringResource(
                    R.string.days_ago_format,
                    ChronoUnit.DAYS.between(habitDate.toLocalDate(), today)
                ),
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        HabitStartCard(
            today = LocalDate.now(),
            habitDate = LocalDateTime.now()
        )
    }
}