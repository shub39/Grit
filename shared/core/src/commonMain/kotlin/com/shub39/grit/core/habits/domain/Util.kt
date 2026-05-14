package com.shub39.grit.core.habits.domain

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun calendarMapStreakShape(
    streakPosition: StreakPosition,
    isFirstDayOfWeek: Boolean,
    isLastDayOfWeek: Boolean,
    isFirstDayOfMonth: Boolean,
    isLastDayOfMonth: Boolean,
): Shape {
    if (streakPosition == StreakPosition.ISOLATED) return CircleShape

    // Calculate left-side corners (topStart, bottomStart)
    val leftRadius =
        when {
            streakPosition == StreakPosition.START -> 20.dp
            isFirstDayOfWeek || isFirstDayOfMonth -> 4.dp
            else -> 0.dp
        }

    // Calculate right-side corners (topEnd, bottomEnd)
    val rightRadius =
        when {
            streakPosition == StreakPosition.END -> 20.dp
            isLastDayOfWeek || isLastDayOfMonth -> 4.dp
            else -> 0.dp
        }

    return RoundedCornerShape(
        topStart = leftRadius,
        bottomStart = leftRadius,
        topEnd = rightRadius,
        bottomEnd = rightRadius,
    )
}

fun heatMapStreakShape(
    streakPosition: StreakPosition,
    isFirstDayOfWeek: Boolean,
    isLastDayOfWeek: Boolean,
): Shape {
    if (streakPosition == StreakPosition.ISOLATED) return CircleShape

    // Calculate top-side corners (topStart, topEnd)
    val topRadius =
        when {
            streakPosition == StreakPosition.START -> 20.dp
            isFirstDayOfWeek -> 4.dp
            else -> 0.dp
        }

    // Calculate bottom-side corners (bottomStart, bottomEnd)
    val bottomRadius =
        when {
            streakPosition == StreakPosition.END -> 20.dp
            isLastDayOfWeek -> 4.dp
            else -> 0.dp
        }

    return RoundedCornerShape(
        topStart = topRadius,
        topEnd = topRadius,
        bottomStart = bottomRadius,
        bottomEnd = bottomRadius,
    )
}
