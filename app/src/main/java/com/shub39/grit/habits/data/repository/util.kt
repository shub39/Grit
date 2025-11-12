package com.shub39.grit.habits.data.repository

import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.domain.WeekDayFrequencyData
import com.shub39.grit.habits.domain.WeeklyComparisonData
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun countCurrentStreak(
    dates: List<LocalDate>,
    eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet()
): Int {
    if (dates.isEmpty()) return 0

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val filteredDates = dates.filter { eligibleWeekdays.contains(it.dayOfWeek) }.sorted()

    if (filteredDates.isEmpty()) return 0

    val lastDate = filteredDates.last()

    // Check if we need to account for eligible days between lastDate and today
    val daysBetween = lastDate.daysUntil(today)
    if (daysBetween > 0) {
        // Check if there are any eligible days we missed between lastDate and today
        var hasEligibleDayMissed = false
        for (i in 1..daysBetween) {
            val checkDate = lastDate.plus(DatePeriod(days = i))
            if (eligibleWeekdays.contains(checkDate.dayOfWeek) && checkDate < today) {
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

fun countBestStreak(
    dates: List<LocalDate>,
    eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet()
): Int {
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

@OptIn(ExperimentalTime::class)
fun prepareLineChartData(
    firstDay: DayOfWeek,
    habitStatuses: List<HabitStatus>
): WeeklyComparisonData {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val totalWeeks = 15

    // Find the start date of the 15-week period
    val startDateOfTodayWeek = today.minus(
        today.dayOfWeek.isoDayNumber - firstDay.isoDayNumber,
        DateTimeUnit.DAY
    )
    val startDateOfPeriod = startDateOfTodayWeek.minus(totalWeeks, DateTimeUnit.WEEK)

    val habitCompletionByWeek = habitStatuses
        .filter { it.date in startDateOfPeriod..today }
        .groupBy {
            // Calculate the start date of the week for the given habit date
            val daysFromFirstDay = (it.date.dayOfWeek.isoDayNumber - firstDay.isoDayNumber + 7) % 7
            it.date.minus(daysFromFirstDay, DateTimeUnit.DAY)
        }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }

    val values = (0..totalWeeks).map { i ->
        // The start of the current week in the loop, starting from 15 weeks ago
        val currentWeekStart = startDateOfPeriod.plus(i, DateTimeUnit.WEEK)

        // The key for the map is the LocalDate representing the start of the week
        val weekKey = currentWeekStart

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
        val weekName = DayOfWeekNames.ENGLISH_ABBREVIATED.names[dayOfWeek.isoDayNumber - 1]

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

private fun areConsecutiveEligibleDays(
    date1: LocalDate,
    date2: LocalDate,
    eligibleWeekdays: Set<DayOfWeek>
): Boolean {
    var checkDate = date1.plus(1, DateTimeUnit.DAY)
    while (checkDate < date2) {
        if (eligibleWeekdays.contains(checkDate.dayOfWeek)) {
            // Found an eligible day between date1 and date2, so they're not consecutive
            return false
        }
        checkDate = checkDate.plus(1, DateTimeUnit.DAY)
    }
    return checkDate == date2
}