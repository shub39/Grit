package com.shub39.grit.core.habits.domain

import kotlinx.serialization.Serializable

typealias WeeklyComparisonData = List<Double>
typealias WeekDayFrequencyData = Map<String, Int>

@Serializable
data class HabitWithAnalytics(
    val habit: Habit,
    val statuses: List<HabitStatus>,
    val weeklyComparisonData: WeeklyComparisonData,
    val weekDayFrequencyData: WeekDayFrequencyData,
    val currentStreak: Int,
    val bestStreak: Int,
    val startedDaysAgo: Long
)