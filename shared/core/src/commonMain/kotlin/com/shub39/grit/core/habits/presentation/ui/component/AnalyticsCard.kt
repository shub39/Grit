package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.utils.blurPossible
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.unlock_plus
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Wrapper card for analytics display
 */
@Composable
fun AnalyticsCard(
    title: String,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    canSeeContent: Boolean = true,
    onPlusClick: () -> Unit = {},
    header:  @Composable (RowScope.() -> Unit) = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )

            header()
        }

        if (!canSeeContent) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .heightIn(300.dp)
                    .fillMaxWidth()
            ) {
                if (blurPossible()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(
                                radius = 10.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                    ) { content() }
                }

                Button(
                    onClick = onPlusClick
                ) { Text(text = stringResource(Res.string.unlock_plus)) }
            }
        } else {
            content()
        }
    }
}