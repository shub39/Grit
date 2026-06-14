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
package com.shub39.grit.web_demo.stubs

import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitRanking
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.habits.HabitWithAnalytics
import com.shub39.grit.core.habits.OverallAnalytics
import com.shub39.grit.core.interfaces.SettingsDatastore
import com.shub39.grit.core.interfaces.ThemeDatastore
import com.shub39.grit.core.now
import com.shub39.grit.core.settings.Sections
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import com.shub39.grit.core.theme.PaletteStyle
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.core.annotation.Single

@Single(binds = [ThemeDatastore::class])
class ThemeDatastoreStub : ThemeDatastore {
    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM)
    private val _seedColor = MutableStateFlow(0)
    private val _amoled = MutableStateFlow(false)
    private val _paletteStyle = MutableStateFlow(PaletteStyle.TONALSPOT)
    private val _materialYou = MutableStateFlow(false)
    private val _fontPref = MutableStateFlow(Fonts.SYSTEM_DEFAULT)

    override suspend fun resetAppTheme() {
        _appTheme.update { AppTheme.SYSTEM }
    }

    override fun getAppThemeFlow(): Flow<AppTheme> = _appTheme.asStateFlow()

    override suspend fun setAppTheme(theme: AppTheme) {
        _appTheme.update { theme }
    }

    override fun getSeedColorFlow(): Flow<Int> = _seedColor.asStateFlow()

    override suspend fun setSeedColor(color: Int) {
        _seedColor.update { color }
    }

    override fun getAmoledPref(): Flow<Boolean> = _amoled.asStateFlow()

    override suspend fun setAmoledPref(pref: Boolean) {
        _amoled.update { pref }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> = _paletteStyle.asStateFlow()

    override suspend fun setPaletteStyle(style: PaletteStyle) {
        _paletteStyle.update { style }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = _materialYou.asStateFlow()

    override suspend fun setMaterialYou(pref: Boolean) {
        _materialYou.update { pref }
    }

    override fun getFontPrefFlow(): Flow<Fonts> = _fontPref.asStateFlow()

    override suspend fun setFontPref(font: Fonts) {
        _fontPref.update { font }
    }
}

@Single(binds = [SettingsDatastore::class])
class SettingsDatastoreStub : SettingsDatastore {
    private val _startOfWeek = MutableStateFlow(DayOfWeek.MONDAY)
    private val _startingSection = MutableStateFlow(Sections.Tasks)
    private val _is24Hr = MutableStateFlow(false)
    private val _notifications = MutableStateFlow(false)
    private val _biometricLock = MutableStateFlow(false)
    private val _taskReorder = MutableStateFlow(false)
    private val _compactView = MutableStateFlow(false)
    private val _lastChangelog = MutableStateFlow("")

    override fun getStartOfTheWeekPref(): Flow<DayOfWeek> = _startOfWeek.asStateFlow()

    override suspend fun setStartOfWeek(day: DayOfWeek) {
        _startOfWeek.update { day }
    }

    override fun getStartingSectionPref(): Flow<Sections> = _startingSection.asStateFlow()

    override suspend fun setStartingPage(page: Sections) {
        _startingSection.update { page }
    }

    override fun getIs24Hr(): Flow<Boolean> = _is24Hr.asStateFlow()

    override suspend fun setIs24Hr(pref: Boolean) {
        _is24Hr.update { pref }
    }

    override fun getNotificationsFlow(): Flow<Boolean> = _notifications.asStateFlow()

    override suspend fun setNotifications(pref: Boolean) {
        _notifications.update { pref }
    }

    override fun getBiometricLockPref(): Flow<Boolean> = _biometricLock.asStateFlow()

    override suspend fun setBiometricPref(pref: Boolean) {
        _biometricLock.update { pref }
    }

    override fun getTaskReorderPref(): Flow<Boolean> = _taskReorder.asStateFlow()

    override suspend fun setTaskReorderPref(pref: Boolean) {
        _taskReorder.update { pref }
    }

    override fun getCompactViewPref(): Flow<Boolean> = _compactView.asStateFlow()

    override suspend fun setCompactView(pref: Boolean) {
        _compactView.update { pref }
    }

    override fun getLastChangelogShown(): Flow<String> = _lastChangelog.asStateFlow()

    override suspend fun updateLastChangelogShown(version: String) {
        _lastChangelog.update { version }
    }
}

@Single(binds = [TaskRepo::class])
class TaskRepoStub : TaskRepo {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    private val _categories =
        MutableStateFlow(listOf(Category(id = 1, name = "General", index = 0, color = "#FFFFFF")))

    init {
        val work = Category(id = 2, name = "Work", index = 1, color = "#4285F4")
        val personal = Category(id = 3, name = "Personal", index = 2, color = "#34A853")
        _categories.update { it + listOf(work, personal) }

        _tasks.update {
            listOf(
                Task(id = 1, categoryId = 1, title = "Welcome to Grit!"),
                Task(id = 2, categoryId = 2, title = "Complete project documentation"),
                Task(id = 3, categoryId = 2, title = "Team meeting", status = true),
                Task(id = 4, categoryId = 3, title = "Buy groceries"),
            )
        }
    }

    override fun getTasksFlow(): Flow<Map<Category, List<Task>>> =
        _tasks.asStateFlow().map { taskList ->
            val categories = _categories.value
            categories.associateWith { category ->
                taskList.filter { it.categoryId == category.id }.sortedBy { it.index }
            }
        }

    override fun getCompletedTasksFlow(): Flow<List<Task>> =
        _tasks.asStateFlow().map { it.filter { task -> task.status } }

    override suspend fun getTasks(): List<Task> = _tasks.value

    override suspend fun getTaskById(id: Long): Task? = _tasks.value.find { it.id == id }

    override suspend fun getCategories(): List<Category> = _categories.value

    override suspend fun updateTaskIndexById(id: Long, index: Int) {
        _tasks.update { list -> list.map { if (it.id == id) it.copy(index = index) else it } }
    }

    override suspend fun upsertTask(task: Task) {
        _tasks.update { list ->
            val existing = list.find { it.id == task.id && it.id != 0L }
            if (existing != null) {
                list.map { if (it.id == task.id) task else it }
            } else {
                val newId = (list.maxOfOrNull { it.id } ?: 0L) + 1
                list + task.copy(id = newId)
            }
        }
    }

    override suspend fun deleteTask(task: Task) {
        _tasks.update { it.filter { t -> t.id != task.id } }
    }

    override suspend fun deleteAllTasks() {
        _tasks.update { emptyList() }
    }

    override suspend fun upsertCategory(category: Category) {
        _categories.update { list ->
            val existing = list.find { it.id == category.id && it.id != 0L }
            if (existing != null) {
                list.map { if (it.id == category.id) category else it }
            } else {
                val newId = (list.maxOfOrNull { it.id } ?: 0L) + 1
                list + category.copy(id = newId)
            }
        }
    }

    override suspend fun deleteCategory(category: Category) {
        _categories.update { it.filter { c -> c.id != category.id } }
        _tasks.update { it.filter { t -> t.categoryId != category.id } }
    }

    override suspend fun deleteAllCategories() {
        _categories.update { emptyList() }
        _tasks.update { emptyList() }
    }
}

@Single(binds = [HabitRepo::class])
class HabitRepoStub(private val datastore: SettingsDatastore) : HabitRepo {
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    private val _statuses = MutableStateFlow<List<HabitStatus>>(emptyList())

    private val _firstDayOfWeek = MutableStateFlow(DayOfWeek.MONDAY)

    init {
        datastore
            .getStartOfTheWeekPref()
            .onEach { day -> _firstDayOfWeek.update { day } }
            .launchIn(CoroutineScope(Dispatchers.Default))

        val today = LocalDate.now()
        val exercise =
            Habit(
                id = 1,
                title = "Exercise",
                description = "30 mins of physical activity",
                time = LocalDateTime(today, LocalTime(8, 0)),
                days = DayOfWeek.entries.toSet(),
                index = 0,
                reminder = true,
            )
        val read =
            Habit(
                id = 2,
                title = "Read",
                description = "Read 10 pages of a book",
                time = LocalDateTime(today, LocalTime(21, 0)),
                days = DayOfWeek.entries.toSet(),
                index = 1,
                reminder = true,
            )
        _habits.update { listOf(exercise, read) }

        _statuses.update {
            val generatedStatuses = mutableListOf<HabitStatus>()
            var statusId = 1L
            listOf(exercise, read).forEach { habit ->
                for (i in 0..180) {
                    val date = today.minus(i, DateTimeUnit.DAY)
                    if (Random.nextFloat() < 0.7f) {
                        generatedStatuses.add(
                            HabitStatus(id = statusId++, habitId = habit.id, date = date)
                        )
                    }
                }
            }
            generatedStatuses
        }
    }

    override suspend fun upsertHabit(habit: Habit) {
        _habits.update { list ->
            val existing = list.find { it.id == habit.id && it.id != 0L }
            if (existing != null) {
                list.map { if (it.id == habit.id) habit else it }
            } else {
                val newId = (list.maxOfOrNull { it.id } ?: 0L) + 1
                list + habit.copy(id = newId)
            }
        }
    }

    override suspend fun deleteHabit(habitId: Long) {
        _habits.update { habits -> habits.filter { it.id != habitId } }
        _statuses.update { statuses -> statuses.filter { it.habitId != habitId } }
    }

    override suspend fun getHabits(): List<Habit> = _habits.value

    override suspend fun getHabitById(id: Long): Habit? = _habits.value.find { it.id == id }

    override suspend fun getHabitStatuses(): List<HabitStatus> = _statuses.value

    override fun getHabitsWithAnalytics(): Flow<List<HabitWithAnalytics>> =
        combine(_habits, _statuses, _firstDayOfWeek) { habits, statuses, firstDay ->
            habits.map { habit ->
                val habitStatuses = statuses.filter { it.habitId == habit.id }
                val dates = habitStatuses.map { it.date }
                HabitWithAnalytics(
                    habit = habit,
                    consistency = calculateConsistency(dates, habit.days),
                    statuses = habitStatuses,
                    weeklyComparisonData = prepareLineChartData(firstDay, habitStatuses),
                    weekDayFrequencyData = prepareWeekDayFrequencyData(dates),
                    currentStreak = countCurrentStreak(dates, habit.days),
                    bestStreak = countBestStreak(dates, habit.days),
                    startedDaysAgo = habit.time.date.daysUntil(LocalDate.now()).toLong(),
                )
            }
        }

    override fun getCompletedHabitIds(): Flow<List<Long>> =
        _statuses.asStateFlow().map {
            it.filter { s -> s.date == LocalDate.now() }.map { s -> s.habitId }.distinct()
        }

    override fun getOverallAnalytics(): Flow<OverallAnalytics> =
        combine(_habits, _statuses) { habits, statuses ->
            val habitConsistencies =
                habits.map { habit ->
                    val dates = statuses.filter { it.habitId == habit.id }.map { it.date }
                    habit.title to calculateConsistency(dates, habit.days)
                }

            val consistencies = habitConsistencies.map { it.second }
            val overallConsistency =
                if (consistencies.isNotEmpty()) consistencies.average().toFloat() else 0f

            val topHabits =
                habitConsistencies
                    .filter { it.second > 0f }
                    .sortedByDescending { it.second }
                    .take(3)
                    .map { HabitRanking(it.first, it.second) }

            OverallAnalytics(
                heatMapData = prepareHeatMapData(statuses),
                weekDayFrequencyData = prepareWeekDayFrequencyData(statuses.map { it.date }),
                consistency = overallConsistency,
                topHabits = topHabits,
            )
        }

    override fun getHabitsWithStatus(): Flow<List<Pair<Habit, Boolean>>> =
        combine(_habits, _statuses) { habits, statuses ->
            habits.map { habit ->
                habit to statuses.any { it.habitId == habit.id && it.date == LocalDate.now() }
            }
        }

    override suspend fun getStatusForHabit(id: Long): List<HabitStatus> =
        _statuses.value.filter { it.habitId == id }

    override suspend fun insertHabitStatus(habitStatus: HabitStatus) {
        _statuses.update { list ->
            val newId = (list.maxOfOrNull { it.id } ?: 0L) + 1
            list + habitStatus.copy(id = newId)
        }
    }

    override suspend fun deleteHabitStatus(habitId: Long, date: LocalDate) {
        _statuses.update { list -> list.filterNot { it.habitId == habitId && it.date == date } }
    }

    override suspend fun getCompletedHabitsForDate(date: LocalDate): List<Habit> {
        val completedIds = _statuses.value.filter { it.date == date }.map { it.habitId }
        return _habits.value.filter { it.id in completedIds }
    }
}

private fun countCurrentStreak(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek>): Int {
    if (dates.isEmpty()) return 0

    val today = LocalDate.now()
    val filteredDates = dates.filter { eligibleWeekdays.contains(it.dayOfWeek) }.sorted()

    if (filteredDates.isEmpty()) return 0

    val lastDate = filteredDates.last()

    val daysBetween = lastDate.daysUntil(today)
    if (daysBetween > 0) {
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

        if (areConsecutiveEligibleDays(currentDate, nextDate, eligibleWeekdays)) {
            streak++
        } else {
            break
        }
    }
    return streak
}

private fun countBestStreak(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek>): Int {
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

private fun prepareLineChartData(
    firstDay: DayOfWeek,
    habitStatuses: List<HabitStatus>,
): List<Double> {
    val today = LocalDate.now()
    val totalWeeks = 52

    val startDateOfTodayWeek =
        today.minus(today.dayOfWeek.isoDayNumber - firstDay.isoDayNumber, DateTimeUnit.DAY)
    val startDateOfPeriod = startDateOfTodayWeek.minus(totalWeeks, DateTimeUnit.WEEK)

    val habitCompletionByWeek =
        habitStatuses
            .filter { it.date in startDateOfPeriod..today }
            .groupBy {
                val daysFromFirstDay =
                    (it.date.dayOfWeek.isoDayNumber - firstDay.isoDayNumber + 7) % 7
                it.date.minus(daysFromFirstDay, DateTimeUnit.DAY)
            }
            .mapValues { (_, statuses) -> statuses.size }

    return (0..totalWeeks).map { i ->
        val currentWeekStart = startDateOfPeriod.plus(i, DateTimeUnit.WEEK)
        (habitCompletionByWeek[currentWeekStart]?.toDouble() ?: 0.0).coerceIn(0.0, 7.0)
    }
}

private fun prepareWeekDayFrequencyData(dates: List<LocalDate>): Map<String, Int> {
    val dayFrequency = dates.groupingBy { it.dayOfWeek }.eachCount()

    return DayOfWeek.entries.associate { dayOfWeek ->
        val weekName = DayOfWeekNames.ENGLISH_ABBREVIATED.names[dayOfWeek.isoDayNumber - 1]
        weekName to (dayFrequency[dayOfWeek] ?: 0)
    }
}

private fun prepareHeatMapData(habitData: List<HabitStatus>): Map<LocalDate, Int> {
    return habitData.map { it.date }.groupingBy { it }.eachCount()
}

private fun areConsecutiveEligibleDays(
    date1: LocalDate,
    date2: LocalDate,
    eligibleWeekdays: Set<DayOfWeek>,
): Boolean {
    var checkDate = date1.plus(1, DateTimeUnit.DAY)
    while (checkDate < date2) {
        if (eligibleWeekdays.contains(checkDate.dayOfWeek)) {
            return false
        }
        checkDate = checkDate.plus(1, DateTimeUnit.DAY)
    }
    return checkDate == date2
}

private fun calculateConsistency(dates: List<LocalDate>, eligibleWeekdays: Set<DayOfWeek>): Float {
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
