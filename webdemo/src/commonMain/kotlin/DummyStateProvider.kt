import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.domain.WeekDayFrequencyData
import com.shub39.grit.core.habits.domain.WeeklyComparisonData
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.utils.now
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object DummyStateProvider {
    private val _habitState = MutableStateFlow(
        HabitState(
            habitsWithAnalytics = listOf(
                HabitWithAnalytics(
                    habit = Habit(
                        id = 1,
                        title = "Morning Walk",
                        description = "A 30-minute walk every morning",
                        time = LocalDateTime.now(),
                        days = DayOfWeek.entries.toSet(),
                        index = 0,
                        reminder = true
                    ),
                    statuses = emptyList(),
                    weeklyComparisonData = emptyList(),
                    weekDayFrequencyData = emptyMap(),
                    currentStreak = 5,
                    bestStreak = 10,
                    startedDaysAgo = 30
                ),
                HabitWithAnalytics(
                    habit = Habit(
                        id = 2,
                        title = "Read a book",
                        description = "Read 20 pages of a book",
                        time = LocalDateTime.now(),
                        days = DayOfWeek.entries.toSet(),
                        index = 1,
                        reminder = false
                    ),
                    statuses = emptyList(),
                    weeklyComparisonData = emptyList(),
                    weekDayFrequencyData = emptyMap(),
                    currentStreak = 3,
                    bestStreak = 8,
                    startedDaysAgo = 25
                )
            )
        )
    )
    private val _taskState = MutableStateFlow(
        TaskState(
            tasks = mapOf(
                Category(id = 1, name = "Work", color = "#FF0000") to listOf(
                    Task(id = 1, categoryId = 1, title = "Finish the report"),
                    Task(id = 2, categoryId = 1, title = "Prepare for the meeting")
                ),
                Category(id = 2, name = "Personal", color = "#00FF00") to listOf(
                    Task(id = 3, categoryId = 2, title = "Buy groceries")
                )
            ),
            currentCategory = Category(id = 1, name = "Work", color = "#FF0000")
        )
    )

    val habitState: StateFlow<HabitState> = _habitState.asStateFlow()
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    fun onHabitAction(action: HabitsAction) {
        when (action) {
            is HabitsAction.AddHabit -> {
                _habitState.update {
                    val newHabitWithAnalytics = HabitWithAnalytics(
                        habit = action.habit,
                        statuses = emptyList(),
                        weeklyComparisonData = emptyList(),
                        weekDayFrequencyData = emptyMap(),
                        currentStreak = 0,
                        bestStreak = 0,
                        startedDaysAgo = 0
                    )
                    it.copy(
                        habitsWithAnalytics = it.habitsWithAnalytics + newHabitWithAnalytics,
                        showHabitAddSheet = false
                    )
                }
            }

            is HabitsAction.DeleteHabit -> {
                _habitState.update { state ->
                    state.copy(habitsWithAnalytics = state.habitsWithAnalytics.filter { it.habit.id != action.habit.id })
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

                    val updatedStatuses = if (isCompleted) {
                        habitWithAnalytics.statuses.filter { it.date != action.date }
                    } else {
                        habitWithAnalytics.statuses + newStatus
                    }

                    val today = LocalDate.now()
                    val updatedCompletedHabitIds = if (action.date == today) {
                        if (isCompleted) {
                            state.completedHabitIds - action.habit.id
                        } else {
                            state.completedHabitIds + action.habit.id
                        }
                    } else {
                        state.completedHabitIds
                    }
                    val dates = updatedStatuses.map { it.date }

                    val updatedHabitWithAnalytics = habitWithAnalytics.copy(
                        statuses = updatedStatuses,
                        weeklyComparisonData = prepareLineChartData(
                            state.startingDay,
                            updatedStatuses
                        ),
                        weekDayFrequencyData = prepareWeekDayFrequencyData(dates),
                        currentStreak = countCurrentStreak(dates, action.habit.days),
                        bestStreak = countBestStreak(dates, action.habit.days)
                    )

                    val updatedHabits = state.habitsWithAnalytics.map {
                        if (it.habit.id == action.habit.id) {
                            updatedHabitWithAnalytics
                        } else {
                            it
                        }
                    }
                    state.copy(
                        habitsWithAnalytics = updatedHabits,
                        completedHabitIds = updatedCompletedHabitIds
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
                    val updatedHabits = state.habitsWithAnalytics.map {
                        if (it.habit.id == action.habit.id) {
                            it.copy(habit = action.habit)
                        } else {
                            it
                        }
                    }
                    state.copy(habitsWithAnalytics = updatedHabits)
                }
            }

            is HabitsAction.OnToggleEditState -> _habitState.update { it.copy(editState = action.pref) }
        }
    }

    fun onTaskAction(action: TaskAction) {
        when (action) {
            is TaskAction.DeleteTask -> {
                _taskState.update { state ->
                    val updatedTasks = state.tasks.mapValues { (_, taskList) ->
                        taskList.filter { it.id == action.task.id }
                    }.toMutableMap()
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
                    state.copy(
                        tasks = newTasks,
                        currentCategory = newTasks.keys.firstOrNull()
                    )
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
                    val updatedTasks = state.tasks.mapValues { (_, taskList) ->
                        taskList.filter { !it.status }
                    }.toMutableMap()
                    state.copy(tasks = updatedTasks)
                }
            }

            is TaskAction.ReorderCategories -> {
                _taskState.update { state ->
                    val reorderedCategories = action.mapping.map {
                        it.second.copy(index = it.first)
                    }.associateWith { category ->
                        state.tasks[state.tasks.keys.find { it.id == category.id }] ?: emptyList()
                    }
                    state.copy(
                        tasks = reorderedCategories,
                        currentCategory = reorderedCategories.keys.firstOrNull()
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
                    val updatedTaskList = if (taskList.any { it.id == newTask.id }) {
                        taskList.map { if (it.id == newTask.id) newTask else it }
                    } else {
                        taskList + newTask
                    }
                    val newTasks = state.tasks.toMutableMap()
                    val completedTasks = if (newTask.status) {
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

    private fun countCurrentStreak(
        dates: List<LocalDate>,
        eligibleWeekdays: Set<DayOfWeek> = DayOfWeek.entries.toSet()
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

    private fun prepareLineChartData(
        firstDay: DayOfWeek,
        habitStatuses: List<HabitStatus>
    ): WeeklyComparisonData {
        val today = LocalDate.now()
        val totalWeeks = 15

        val startDateOfTodayWeek = today.minus(
            today.dayOfWeek.isoDayNumber - firstDay.isoDayNumber,
            DateTimeUnit.DAY
        )
        val startDateOfPeriod = startDateOfTodayWeek.minus(totalWeeks, DateTimeUnit.WEEK)

        val habitCompletionByWeek = habitStatuses
            .filter { it.date in startDateOfPeriod..today }
            .groupBy {
                val daysFromFirstDay =
                    (it.date.dayOfWeek.isoDayNumber - firstDay.isoDayNumber + 7) % 7
                it.date.minus(daysFromFirstDay, DateTimeUnit.DAY)
            }
            .mapValues { (_, habitStatuses) -> habitStatuses.size }

        val values = (0..totalWeeks).map { i ->
            val currentWeekStart = startDateOfPeriod.plus(i, DateTimeUnit.WEEK)
            (habitCompletionByWeek[currentWeekStart]?.toDouble() ?: 0.0).coerceIn(0.0, 7.0)
        }
        return values
    }

    private fun prepareWeekDayFrequencyData(
        dates: List<LocalDate>
    ): WeekDayFrequencyData {
        val dayFrequency = dates
            .groupingBy { it.dayOfWeek }
            .eachCount()

        return DayOfWeek.entries.associate {
            val weekName = DayOfWeekNames.ENGLISH_ABBREVIATED.names[it.isoDayNumber - 1]

            weekName to (dayFrequency[it] ?: 0)
        }
    }

    private fun areConsecutiveEligibleDays(
        date1: LocalDate,
        date2: LocalDate,
        eligibleWeekdays: Set<DayOfWeek>
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
}