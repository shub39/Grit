package com.shub39.grit.habits.domain

import java.time.LocalDate

typealias HeatMapData = Map<LocalDate, Int>
typealias WeeklyGraphData = Map<Habit, WeeklyComparisonData>

data class OverallAnalytics(
    val heatMapData: HeatMapData = emptyMap(),
    val weekDayFrequencyData: WeekDayFrequencyData = emptyMap(),
    val weeklyGraphData: WeeklyGraphData = emptyMap()
)
