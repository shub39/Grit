package com.shub39.grit.habits.presentation.component

import com.shub39.grit.habits.domain.HabitStatus
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

fun prepareWeeklyData(habitStatusEntityList: List<HabitStatus>): List<Pair<Int, Int>> {
    if (habitStatusEntityList.isEmpty()) return emptyList()

    val weekFields = WeekFields.of(Locale.getDefault())
    val startDate = habitStatusEntityList.minByOrNull { it.date }?.date ?: LocalDate.now()
    val endDate = habitStatusEntityList.maxByOrNull { it.date }?.date ?: LocalDate.now()

    val startWeek = startDate.get(weekFields.weekOfYear())
    val endWeek = endDate.get(weekFields.weekOfYear())

    val habitCompletionByWeek = habitStatusEntityList
        .groupBy { it.date.get(weekFields.weekOfYear()) }
        .mapValues { (_, habitStatuses) -> habitStatuses.size }

    return (startWeek..endWeek).map { week ->
        week to (habitCompletionByWeek[week] ?: 0)
    }
}