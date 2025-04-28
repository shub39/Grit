package com.shub39.grit.habits.presentation.component

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.shub39.grit.habits.domain.HabitStatus
import ir.ehsannarmani.compose_charts.models.Line
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

fun prepareLineChartData(habitstatuses: List<HabitStatus>): List<Line> {
    if (habitstatuses.isEmpty()) return emptyList()

    val weekFields = WeekFields.of(Locale.getDefault())
    val startDate = habitstatuses.minByOrNull { it.date }?.date ?: LocalDate.now()
    val endDate = habitstatuses.maxByOrNull { it.date }?.date ?: LocalDate.now()

    val startWeek = startDate.get(weekFields.weekOfYear())
    val endWeek = endDate.get(weekFields.weekOfYear())

    val habitCompletionByWeek = habitstatuses
        .groupBy { it.date.get(weekFields.weekOfYear()) }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }

    val values = (startWeek..endWeek).map { week ->
        habitCompletionByWeek[week]?.toDouble() ?: 0.0
    }

    return listOf(
        Line(
            label = "Weekly Activity",
            values = values,
            color = Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784))),
            firstGradientFillColor = Color(0xFF4CAF50).copy(alpha = 0.4f),
            secondGradientFillColor = Color(0xFF81C784).copy(alpha = 0.2f),
        )
    )
}

fun getOrdinalSuffix(day: Int): String {
    return when {
        day in 11..13 -> "${day}th"
        else -> when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }
}

fun formatDateWithOrdinal(date: LocalDate): String {
    val day = date.dayOfMonth
    val month = date.format(DateTimeFormatter.ofPattern("MMMM"))
    val year = date.year

    return "${getOrdinalSuffix(day)} $month $year"
}