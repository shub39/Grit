package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.utils.AllPreviews
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.best_streak
import grit.shared.core.generated.resources.heat
import grit.shared.core.generated.resources.streak
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitStreakCard(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    val daysLeft by animateFloatAsState(
        targetValue = if (bestStreak > 0) {
            currentStreak.toFloat() / bestStreak.toFloat()
        } else {
            0f
        }
    )

    val infiniteTransition =
        rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "infinite rotation"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        shape = CircleShape
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val width = size.width * daysLeft

                        clipRect(
                            right = width,
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .then(
                                if (daysLeft >= 1) {
                                    Modifier.graphicsLayer {
                                        rotationZ = rotation
                                    }
                                } else Modifier
                            )
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = VerySunny.toShape()
                            )
                    )

                    Icon(
                        imageVector = vectorResource(Res.drawable.heat),
                        contentDescription = "Flag",
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(Res.string.streak),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = currentStreak.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(Res.string.best_streak, bestStreak),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@AllPreviews
@Composable
private fun Preview() {
    GritTheme {
        HabitStreakCard(
            currentStreak = 20,
            bestStreak = 20,
        )
    }
}