package com.shub39.grit.habits.data.repository

import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.domain.WeekDayFrequencyData
import com.shub39.grit.habits.domain.WeeklyComparisonData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

fun countCurrentStreak(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet()): Int {
    if (dates.isEmpty()) return 0

    val today = LocalDate.now()
    val filteredDates = dates.filter { eligibleWeekdays.contains(it.dayOfWeek) }.sorted()

    if (filteredDates.isEmpty()) return 0

    val lastDate = filteredDates.last()

    // Check if we need to account for eligible days between lastDate and today
    val daysBetween = ChronoUnit.DAYS.between(lastDate, today)
    if (daysBetween > 0) {
        // Check if there are any eligible days we missed between lastDate and today
        var hasEligibleDayMissed = false
        for (i in 1L..daysBetween) {
            val checkDate = lastDate.plusDays(i)
            if (eligibleWeekdays.contains(checkDate.dayOfWeek) && checkDate.isBefore(today)) {
                hasEligibleDayMissed = true
                break
            }
        }
        if (hasEligibleDayMissed) return 0

        // If today is not eligible, check if we missed any eligible days
        if (!eligibleWeekdays.contains(today.dayOfWeek) && daysBetween > 1) {
            return 0
        }
    }

    var streak = 1
    for (i in filteredDates.size - 2 downTo 0) {
        val currentDate = filteredDates[i]
        val nextDate = filteredDates[i + 1]

        // Check if these are consecutive eligible days
        if (areConsecutiveEligibleDays(currentDate, nextDate, eligibleWeekdays)) {
            streak++
        } else {
            break
        }
    }
    return streak
}

fun countBestStreak(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet()): Int {
    if (dates.isEmpty()) return 0

    val filteredDates = dates.filter { eligibleWeekdays.contains(it.dayOfWeek) }.sorted()
    if (filteredDates.isEmpty()) return 0

    var maxConsecutive = 1
    var currentConsecutive = 1

    for (i in 1 until filteredDates.size) {
        val previousDate = filteredDates[i - 1]
        val currentDate = filteredDates[i]

        if (areConsecutiveEligibleDays(previousDate, currentDate, eligibleWeekdays)) {
            currentConsecutive++
        } else {
            maxConsecutive = maxOf(maxConsecutive, currentConsecutive)
            currentConsecutive = 1
        }
    }

    return maxOf(maxConsecutive, currentConsecutive)
}

fun prepareLineChartData(
    firstDay: DayOfWeek,
    habitstatuses: List<HabitStatus>
): WeeklyComparisonData {
    val today = LocalDate.now()
    val weekFields = WeekFields.of(firstDay, 1)
    val totalWeeks = 15

    val startDateOfPeriod = today.minusWeeks(totalWeeks.toLong()).with(weekFields.dayOfWeek(), 1)

    val habitCompletionByWeek = habitstatuses
        .filter { !it.date.isBefore(startDateOfPeriod) && !it.date.isAfter(today) }
        .groupBy {
            val yearOfWeek = it.date.get(weekFields.weekBasedYear())
            val weekOfYear = it.date.get(weekFields.weekOfWeekBasedYear())
            yearOfWeek * 100 + weekOfYear
        }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }
    val values = (0..totalWeeks).map { i ->
        val currentWeekStart = today.minusWeeks(totalWeeks - i.toLong()).with(weekFields.dayOfWeek(), 1)
        val yearOfWeek = currentWeekStart.get(weekFields.weekBasedYear())
        val weekOfYear = currentWeekStart.get(weekFields.weekOfWeekBasedYear())
        val weekKey = yearOfWeek * 100 + weekOfYear
        (habitCompletionByWeek[weekKey]?.toDouble() ?: 0.0).coerceIn(0.0, 7.0)
    }
    return values
}

fun prepareWeekDayFrequencyData(
    dates: List<LocalDate>
): WeekDayFrequencyData {
    val dayFrequency = dates
        .groupingBy { it.dayOfWeek }
        .eachCount()

    return DayOfWeek.entries.associate { dayOfWeek ->
        val weekName = dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        ).toString()

        weekName to dayFrequency.getOrDefault(dayOfWeek, 0)
    }
}

fun prepareHeatMapData(
    habitData: List<HabitStatus>
): Map<LocalDate, Int> {
    val allDates = habitData.map { it.date }
    val dateFrequency = allDates
        .groupingBy { it }
        .eachCount()

    return dateFrequency
}

private fun areConsecutiveEligibleDays(date1: LocalDate, date2: LocalDate, eligibleWeekdays: Set<DayOfWeek>): Boolean {
    var checkDate = date1.plusDays(1)
    while (checkDate.isBefore(date2)) {
        if (eligibleWeekdays.contains(checkDate.dayOfWeek)) {
            // Found an eligible day between date1 and date2, so they're not consecutive
            return false
        }
        checkDate = checkDate.plusDays(1)
    }
    return checkDate == date2
}