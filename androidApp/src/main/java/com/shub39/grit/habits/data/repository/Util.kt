/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.habits.data.repository

import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.habits.WeekDayFrequencyData
import com.shub39.grit.core.habits.WeeklyComparisonData
import com.shub39.grit.core.now
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun countCurrentStreak(
    dates: List<LocalDate>,
    eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
): Int {
    if (dates.isEmpty()) return 0

    val today = LocalDate.now()
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
    eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
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

fun prepareLineChartData(
    firstDay: DayOfWeek,
    habitStatuses: List<HabitStatus>,
): WeeklyComparisonData {
    val today = LocalDate.now()
    val totalWeeks = 52

    // Find the start date of the 15-week period
    val startDateOfTodayWeek =
        today.minus(today.dayOfWeek.isoDayNumber - firstDay.isoDayNumber, DateTimeUnit.DAY)
    val startDateOfPeriod = startDateOfTodayWeek.minus(totalWeeks, DateTimeUnit.WEEK)

    val habitCompletionByWeek =
        habitStatuses
            .filter { it.date in startDateOfPeriod..today }
            .groupBy {
                // Calculate the start date of the week for the given habit date
                val daysFromFirstDay =
                    (it.date.dayOfWeek.isoDayNumber - firstDay.isoDayNumber + 7) % 7
                it.date.minus(daysFromFirstDay, DateTimeUnit.DAY)
            }
            .mapValues { (_, habitStatuses) -> habitStatuses.size }

    val values =
        (0..totalWeeks).map { i ->
            // The start of the current week in the loop, starting from 15 weeks ago
            val currentWeekStart = startDateOfPeriod.plus(i, DateTimeUnit.WEEK)

            // The key for the map is the LocalDate representing the start of the week
            val weekKey = currentWeekStart

            (habitCompletionByWeek[weekKey]?.toDouble() ?: 0.0).coerceIn(0.0, 7.0)
        }
    return values
}

fun prepareWeekDayFrequencyData(
    dates: List<LocalDate>,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): WeekDayFrequencyData {
    val dayFrequency = dates.groupingBy { it.dayOfWeek }.eachCount()

    val orderedDays = DayOfWeek.entries.toMutableList()
    val index = orderedDays.indexOf(firstDayOfWeek)
    val rotatedDays = orderedDays.subList(index, orderedDays.size) + orderedDays.subList(0, index)

    return rotatedDays.associate { dayOfWeek ->
        val weekName = DayOfWeekNames.ENGLISH_ABBREVIATED.names[dayOfWeek.isoDayNumber - 1]

        weekName to (dayFrequency[dayOfWeek] ?: 0)
    }
}

fun prepareHeatMapData(habitData: List<HabitStatus>): Map<LocalDate, Int> {
    val allDates = habitData.map { it.date }
    val dateFrequency = allDates.groupingBy { it }.eachCount()

    return dateFrequency
}

private fun areConsecutiveEligibleDays(
    date1: LocalDate,
    date2: LocalDate,
    eligibleWeekdays: Set<DayOfWeek>,
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

fun calculateConsistency(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek>): Float {
    val eligibleDates = dates.filter { it.dayOfWeek in eligibleWeekdays }
    val firstCompletionDate = eligibleDates.minOrNull() ?: return 0f
    val today = LocalDate.now()

    var totalEligibleDays = 0
    var current = firstCompletionDate
    while (current <= today) {
        if (current.dayOfWeek in eligibleWeekdays) {
            totalEligibleDays++
        }
        current = current.plus(1, DateTimeUnit.DAY)
    }

    return if (totalEligibleDays > 0) eligibleDates.size.toFloat() / totalEligibleDays else 0f
}
