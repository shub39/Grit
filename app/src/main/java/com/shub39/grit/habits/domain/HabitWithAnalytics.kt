package com.shub39.grit.habits.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

typealias WeeklyComparisonData = List<Double>
typealias WeekDayFrequencyData = Map<String, Int>

@Stable
@Immutable
data class HabitWithAnalytics(
    val habit: Habit,
    val statuses: List<HabitStatus>,
    val weeklyComparisonData: WeeklyComparisonData,
    val weekDayFrequencyData: WeekDayFrequencyData,
    val currentStreak: Int,
    val bestStreak: Int,
    val startedDaysAgo: Int
)