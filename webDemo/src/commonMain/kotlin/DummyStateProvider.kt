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
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRanking
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.OverallAnalytics
import com.shub39.grit.core.habits.domain.WeekDayFrequencyData
import com.shub39.grit.core.habits.domain.WeeklyComparisonData
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.now
import com.shub39.grit.core.settings.presentation.SettingsAction
import com.shub39.grit.core.settings.presentation.SettingsState
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

@OptIn(ExperimentalTime::class)
object DummyStateProvider {
    private val _habitState =
        MutableStateFlow(
            HabitState(habitsWithAnalytics = createDummyHabits()).let { state ->
                val today = LocalDate.now()
                val completedHabitIds =
                    state.habitsWithAnalytics
                        .filter { it.statuses.any { it.date == today } }
                        .map { it.habit.id }
                state.copy(
                    completedHabitIds = completedHabitIds,
                    overallAnalytics = calculateOverallAnalytics(state.habitsWithAnalytics),
                )
            }
        )

    private fun createDummyHabits(): List<HabitWithAnalytics> {
        val morningWalkHabit =
            Habit(
                id = 1,
                title = "Morning Walk",
                description = "A 30-minute walk every morning",
                time =
                    LocalDateTime(
                        date = LocalDate.now().minus(365, DateTimeUnit.DAY),
                        time = LocalTime.now(),
                    ),
                days = DayOfWeek.entries.toSet(),
                index = 0,
                reminder = true,
            )
        val readBookHabit =
            Habit(
                id = 2,
                title = "Read a book",
                description = "Read 20 pages of a book",
                time =
                    LocalDateTime(
                        date = LocalDate.now().minus(365, DateTimeUnit.DAY),
                        time = LocalTime.now(),
                    ),
                days = DayOfWeek.entries.toSet(),
                index = 1,
                reminder = false,
            )

        return listOf(
            createHabitWithAnalytics(morningWalkHabit, 365),
            createHabitWithAnalytics(readBookHabit, 366),
        )
    }

    private fun createHabitWithAnalytics(habit: Habit, daysAgo: Int): HabitWithAnalytics {
        val statuses = generateDummyStatuses(habit.id, daysAgo)
        val dates = statuses.map { it.date }
        val currentStreak = countCurrentStreak(dates, habit.days)
        val bestStreak = countBestStreak(dates, habit.days)

        return HabitWithAnalytics(
            habit = habit,
            statuses = statuses,
            weeklyComparisonData = prepareLineChartData(DayOfWeek.MONDAY, statuses),
            weekDayFrequencyData = prepareWeekDayFrequencyData(dates),
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            startedDaysAgo = daysAgo.toLong(),
            consistency = calculateConsistency(dates, habit.days),
        )
    }

    private fun generateDummyStatuses(habitId: Long, days: Int): List<HabitStatus> {
        val today = LocalDate.now()
        val statuses = mutableListOf<HabitStatus>()
        val random = Random(habitId) // Consistent dummy data per habit
        for (i in 0 until days) {
            val date = today.minus(i, DateTimeUnit.DAY)
            // 70-80% completion rate for dummy data
            if (random.nextFloat() > 0.25) {
                statuses.add(HabitStatus(habitId = habitId, date = date))
            }
        }
        return statuses
    }

    private val _taskState =
        MutableStateFlow(
            TaskState(
                tasks =
                    mapOf(
                        Category(id = 1, name = "Work", color = "#FF0000") to
                            listOf(
                                Task(id = 1, categoryId = 1, title = "Finish the report"),
                                Task(id = 2, categoryId = 1, title = "Prepare for the meeting"),
                            ),
                        Category(id = 2, name = "Personal", color = "#00FF00") to
                            listOf(Task(id = 3, categoryId = 2, title = "Buy groceries")),
                    ),
                currentCategory = Category(id = 1, name = "Work", color = "#FF0000"),
            )
        )

    val habitState: StateFlow<HabitState> = _habitState.asStateFlow()
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    fun onSettingsAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ChangeAppTheme -> {
                _settingsState.update { it.copy(theme = it.theme.copy(appTheme = action.appTheme)) }
            }

            is SettingsAction.ChangeFontPref -> {
                _settingsState.update { it.copy(theme = it.theme.copy(font = action.font)) }
            }

            is SettingsAction.ChangeSeedColor -> {
                _settingsState.update { it.copy(theme = it.theme.copy(seedColor = action.color)) }
            }

            is SettingsAction.ChangePaletteStyle -> {
                _settingsState.update {
                    it.copy(theme = it.theme.copy(paletteStyle = action.style))
                }
            }

            is SettingsAction.ChangeIs24Hr -> {
                _settingsState.update { it.copy(is24Hr = action.pref) }
            }

            is SettingsAction.ChangeStartOfTheWeek -> {
                _settingsState.update { it.copy(startOfTheWeek = action.pref) }
            }

            is SettingsAction.ChangeStartingPage -> {
                _settingsState.update { it.copy(startingPage = action.page) }
            }

            is SettingsAction.ChangePauseNotifications -> {
                _settingsState.update { it.copy(pauseNotifications = action.pref) }
            }

            is SettingsAction.ChangeReorderTasks -> {
                _settingsState.update { it.copy(reorderTasks = action.pref) }
            }

            is SettingsAction.ChangeBiometricLock -> {
                _settingsState.update { it.copy(isBiometricLockOn = action.pref) }
            }

            is SettingsAction.ChangeAmoled -> {
                _settingsState.update { it.copy(theme = it.theme.copy(isAmoled = action.pref)) }
            }

            is SettingsAction.ChangeMaterialYou -> {
                _settingsState.update {
                    it.copy(theme = it.theme.copy(isMaterialYou = action.pref))
                }
            }

            else -> {}
        }
    }

    fun onHabitAction(action: HabitsAction) {
        when (action) {
            is HabitsAction.AddHabit -> {
                _habitState.update {
                    val newHabitWithAnalytics =
                        HabitWithAnalytics(
                            habit = action.habit,
                            statuses = emptyList(),
                            weeklyComparisonData = emptyList(),
                            weekDayFrequencyData = emptyMap(),
                            currentStreak = 0,
                            bestStreak = 0,
                            startedDaysAgo = 0,
                            consistency = 0f,
                        )
                    val updatedHabits = it.habitsWithAnalytics + newHabitWithAnalytics
                    it.copy(
                        habitsWithAnalytics = updatedHabits,
                        overallAnalytics = calculateOverallAnalytics(updatedHabits),
                        showHabitAddSheet = false,
                    )
                }
            }

            is HabitsAction.DeleteHabit -> {
                _habitState.update { state ->
                    val updatedHabits =
                        state.habitsWithAnalytics.filter { it.habit.id != action.habit.id }
                    state.copy(
                        habitsWithAnalytics = updatedHabits,
                        overallAnalytics = calculateOverallAnalytics(updatedHabits),
                    )
                }
            }

            HabitsAction.DismissAddHabitDialog -> {
                _habitState.update { it.copy(showHabitAddSheet = false) }
            }

            is HabitsAction.InsertStatus -> {
                _habitState.update { state ->
                    val habitWithAnalytics =
                        state.habitsWithAnalytics.find { it.habit.id == action.habit.id }
                            ?: return@update state
                    val newStatus = HabitStatus(habitId = action.habit.id, date = action.date)
                    val isCompleted = habitWithAnalytics.statuses.any { it.date == action.date }

                    val updatedStatuses =
                        if (isCompleted) {
                            habitWithAnalytics.statuses.filter { it.date != action.date }
                        } else {
                            habitWithAnalytics.statuses + newStatus
                        }

                    val today = LocalDate.now()
                    val updatedCompletedHabitIds =
                        if (action.date == today) {
                            if (isCompleted) {
                                state.completedHabitIds - action.habit.id
                            } else {
                                state.completedHabitIds + action.habit.id
                            }
                        } else {
                            state.completedHabitIds
                        }
                    val dates = updatedStatuses.map { it.date }

                    val updatedHabitWithAnalytics =
                        habitWithAnalytics.copy(
                            statuses = updatedStatuses,
                            weeklyComparisonData =
                                prepareLineChartData(state.startingDay, updatedStatuses),
                            weekDayFrequencyData = prepareWeekDayFrequencyData(dates),
                            currentStreak = countCurrentStreak(dates, action.habit.days),
                            bestStreak = countBestStreak(dates, action.habit.days),
                            consistency = calculateConsistency(dates, action.habit.days),
                        )

                    val updatedHabits =
                        state.habitsWithAnalytics.map {
                            if (it.habit.id == action.habit.id) {
                                updatedHabitWithAnalytics
                            } else {
                                it
                            }
                        }
                    state.copy(
                        habitsWithAnalytics = updatedHabits,
                        completedHabitIds = updatedCompletedHabitIds,
                        overallAnalytics = calculateOverallAnalytics(updatedHabits),
                    )
                }
            }

            HabitsAction.OnAddHabitClicked -> {
                _habitState.update { it.copy(showHabitAddSheet = true) }
            }

            is HabitsAction.OnToggleCompactView -> {
                _habitState.update { it.copy(compactHabitView = action.pref) }
            }

            is HabitsAction.PrepareAnalytics -> {
                _habitState.update { it.copy(analyticsHabitId = action.habit?.id) }
            }

            is HabitsAction.ReorderHabits -> {}

            is HabitsAction.OnTransientHabitReorder -> {
                val currentList = _habitState.value.habitsWithAnalytics.toMutableList()
                currentList.add(action.to, currentList.removeAt(action.from))
                _habitState.update { it.copy(habitsWithAnalytics = currentList) }
            }

            is HabitsAction.UpdateHabit -> {
                _habitState.update { state ->
                    val updatedHabits =
                        state.habitsWithAnalytics.map {
                            if (it.habit.id == action.habit.id) {
                                it.copy(habit = action.habit)
                            } else {
                                it
                            }
                        }
                    state.copy(
                        habitsWithAnalytics = updatedHabits,
                        overallAnalytics = calculateOverallAnalytics(updatedHabits),
                    )
                }
            }

            is HabitsAction.OnToggleEditState ->
                _habitState.update { it.copy(editState = action.pref) }

            is HabitsAction.FetchCompletedHabitsForDate -> {
                _habitState.update { state ->
                    val completedHabits =
                        state.habitsWithAnalytics
                            .filter { it.statuses.any { status -> status.date == action.date } }
                            .map { it.habit.title }

                    state.copy(
                        overallAnalytics =
                            state.overallAnalytics.copy(
                                completedHabits =
                                    if (completedHabits.isNotEmpty()) {
                                        action.date to completedHabits
                                    } else null
                            )
                    )
                }
            }
        }
    }

    fun onTaskAction(action: TaskAction) {
        when (action) {
            is TaskAction.DeleteTask -> {
                _taskState.update { state ->
                    val updatedTasks =
                        state.tasks
                            .mapValues { (_, taskList) ->
                                taskList.filter { it.id == action.task.id }
                            }
                            .toMutableMap()
                    state.copy(tasks = updatedTasks)
                }
            }

            is TaskAction.AddCategory -> {
                _taskState.update { state ->
                    val existingCategory = state.tasks.keys.find { it.id == action.category.id }
                    val newTasks = state.tasks.toMutableMap()

                    if (existingCategory != null) {
                        val tasksForCategory = newTasks.remove(existingCategory) ?: emptyList()
                        newTasks[action.category] = tasksForCategory
                    } else {
                        newTasks[action.category.copy(id = Random.nextLong())] = emptyList()
                    }
                    state.copy(tasks = newTasks, currentCategory = newTasks.keys.firstOrNull())
                }
            }

            is TaskAction.ChangeCategory -> {
                _taskState.update { it.copy(currentCategory = action.category) }
            }

            is TaskAction.DeleteCategory -> {
                _taskState.update { it.copy(tasks = it.tasks - action.category) }
            }

            TaskAction.DeleteTasks -> {
                _taskState.update { state ->
                    val updatedTasks =
                        state.tasks
                            .mapValues { (_, taskList) -> taskList.filter { !it.status } }
                            .toMutableMap()
                    state.copy(tasks = updatedTasks)
                }
            }

            is TaskAction.ReorderCategories -> {
                _taskState.update { state ->
                    val reorderedCategories =
                        action.mapping
                            .map { it.second.copy(index = it.first) }
                            .associateWith { category ->
                                state.tasks[state.tasks.keys.find { it.id == category.id }]
                                    ?: emptyList()
                            }
                    state.copy(
                        tasks = reorderedCategories,
                        currentCategory = reorderedCategories.keys.firstOrNull(),
                    )
                }
            }

            is TaskAction.ReorderTasks -> {
                _taskState.update { state ->
                    if (action.mapping.isEmpty()) return@update state
                    val categoryId = action.mapping.first().second.categoryId
                    val category =
                        state.tasks.keys.find { it.id == categoryId } ?: return@update state

                    val reorderedTasks = action.mapping.sortedBy { it.first }.map { it.second }
                    val newTasks = state.tasks.toMutableMap()
                    newTasks[category] = reorderedTasks
                    state.copy(tasks = newTasks)
                }
            }

            is TaskAction.UpsertTask -> {
                _taskState.update { state ->
                    val newTask =
                        if (action.task.id !in state.tasks.values.flatten().map { it.id }) {
                            action.task.copy(id = Random.nextLong())
                        } else action.task
                    val category =
                        state.tasks.keys.find { it.id == newTask.categoryId } ?: return@update state
                    val taskList = state.tasks[category] ?: emptyList()
                    val updatedTaskList =
                        if (taskList.any { it.id == newTask.id }) {
                            taskList.map { if (it.id == newTask.id) newTask else it }
                        } else {
                            taskList + newTask
                        }
                    val newTasks = state.tasks.toMutableMap()
                    val completedTasks =
                        if (newTask.status) {
                            state.completedTasks + newTask
                        } else {
                            state.completedTasks.filter { it.id != newTask.id }
                        }
                    newTasks[category] = updatedTaskList
                    state.copy(tasks = newTasks, completedTasks = completedTasks)
                }
            }
        }
    }

    private fun calculateOverallAnalytics(
        habitsWithAnalytics: List<HabitWithAnalytics>
    ): OverallAnalytics {
        val allStatuses = habitsWithAnalytics.flatMap { it.statuses }

        val heatMapData = allStatuses.groupingBy { it.date }.eachCount()

        val weekDayFrequencyData = prepareWeekDayFrequencyData(allStatuses.map { it.date })

        val today = LocalDate.now()
        val completedHabits =
            habitsWithAnalytics
                .filter { it.statuses.any { status -> status.date == today } }
                .map { it.habit.title }

        val overallConsistency =
            if (habitsWithAnalytics.isNotEmpty()) {
                habitsWithAnalytics.map { it.consistency }.average().toFloat()
            } else 0f

        val topHabits =
            habitsWithAnalytics
                .filter { it.consistency > 0f }
                .sortedByDescending { it.consistency }
                .take(3)
                .map { HabitRanking(it.habit.title, it.consistency) }

        return OverallAnalytics(
            heatMapData = heatMapData,
            weekDayFrequencyData = weekDayFrequencyData,
            completedHabits = if (completedHabits.isNotEmpty()) today to completedHabits else null,
            consistency = overallConsistency,
            topHabits = topHabits,
        )
    }

    private fun countCurrentStreak(
        dates: List<LocalDate>,
        eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    ): Int {
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

            if (!eligibleWeekdays.contains(today.dayOfWeek) && daysBetween > 1) {
                return 0
            }
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

    private fun countBestStreak(
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

    private fun prepareLineChartData(
        firstDay: DayOfWeek,
        habitStatuses: List<HabitStatus>,
    ): WeeklyComparisonData {
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
                .mapValues { (_, habitStatuses) -> habitStatuses.size }

        val values =
            (0..totalWeeks).map { i ->
                val currentWeekStart = startDateOfPeriod.plus(i, DateTimeUnit.WEEK)
                (habitCompletionByWeek[currentWeekStart]?.toDouble() ?: 0.0).coerceIn(0.0, 7.0)
            }
        return values
    }

    private fun prepareWeekDayFrequencyData(dates: List<LocalDate>): WeekDayFrequencyData {
        val dayFrequency = dates.groupingBy { it.dayOfWeek }.eachCount()

        return DayOfWeek.entries.associate {
            val weekName = DayOfWeekNames.ENGLISH_ABBREVIATED.names[it.isoDayNumber - 1]

            weekName to (dayFrequency[it] ?: 0)
        }
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

    private fun calculateConsistency(
        dates: List<LocalDate>,
        eligibleWeekdays: Set<DayOfWeek>,
    ): Float {
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
}
