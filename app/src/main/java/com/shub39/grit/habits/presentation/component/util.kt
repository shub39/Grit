package com.shub39.grit.habits.presentation.component

import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

fun prepareLineChartData(
    firstDay: DayOfWeek,
    habitstatuses: List<HabitStatus>
): List<Double> {
    if (habitstatuses.isEmpty()) return emptyList()

    val weekFields = WeekFields.of(firstDay, 1)
    val startDate = habitstatuses.minByOrNull { it.date }?.date ?: LocalDate.now()
    val endDate = habitstatuses.maxByOrNull { it.date }?.date ?: LocalDate.now()

    val startWeek = startDate.get(weekFields.weekOfYear())
    val endWeek = endDate.get(weekFields.weekOfYear())

    val habitCompletionByWeek = habitstatuses
        .groupBy { it.date.get(weekFields.weekOfYear()) }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }

    val values = (startWeek..endWeek).map { week ->
        habitCompletionByWeek[week]?.toDouble() ?: 0.0
    }.takeLast(10)

    return if (values.size < 2) emptyList() else values
}

fun prepareHeatMapData(
    habitData: Map<Habit, List<HabitStatus>>
):  Map<LocalDate, Int> {
    val allDates = habitData.values.flatten().map { it.date }
    val dateFrequency = allDates.groupingBy { it }
        .eachCount()

    return dateFrequency
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