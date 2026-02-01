package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.presentation.formatDateWithOrdinal
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.days_ago_format
import grit.shared.core.generated.resources.flag_circle
import grit.shared.core.generated.resources.started_on
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitStartCard(
    habit: Habit,
    startedDaysAgo: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = Sunny.toShape()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.flag_circle),
                    contentDescription = "Flag",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.started_on),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = formatDateWithOrdinal(habit.time.date),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(
                        Res.string.days_ago_format,
                        startedDaysAgo
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}