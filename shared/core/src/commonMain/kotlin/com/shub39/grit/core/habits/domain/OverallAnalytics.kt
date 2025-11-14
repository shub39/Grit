package com.shub39.grit.core.habits.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

typealias HeatMapData = Map<LocalDate, Int>
typealias WeeklyGraphData = Map<Habit, WeeklyComparisonData>

@Serializable
data class OverallAnalytics(
    val heatMapData: HeatMapData = emptyMap(),
    val weekDayFrequencyData: WeekDayFrequencyData = emptyMap(),
    val weeklyGraphData: WeeklyGraphData = emptyMap()
)