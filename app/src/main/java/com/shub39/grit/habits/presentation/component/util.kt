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
    val today = LocalDate.now()
    val weekFields = WeekFields.of(firstDay, 1)

    // Calculate the start date of the 10-week period
    val startDateOfPeriod = today.minusWeeks(9).with(weekFields.dayOfWeek(), 1)

    val habitCompletionByWeek = habitstatuses
        .filter { !it.date.isBefore(startDateOfPeriod) && !it.date.isAfter(today) }
        .groupBy {
            // Normalize the week year to handle year boundaries correctly
            val yearOfWeek = it.date.get(weekFields.weekBasedYear())
            val weekOfYear = it.date.get(weekFields.weekOfWeekBasedYear())
            yearOfWeek * 100 + weekOfYear // Unique key for each week across years
        }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }
    val values = (0..9).map { i ->
        val currentWeekStart = today.minusWeeks(9 - i.toLong()).with(weekFields.dayOfWeek(), 1)
        val yearOfWeek = currentWeekStart.get(weekFields.weekBasedYear())
        val weekOfYear = currentWeekStart.get(weekFields.weekOfWeekBasedYear())
        val weekKey = yearOfWeek * 100 + weekOfYear
        habitCompletionByWeek[weekKey]?.toDouble() ?: 0.0
    }
    return if (values.filter { it > 0.0 }.size < 2 && values.size <=10) values else if (values.size <2) emptyList() else values
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