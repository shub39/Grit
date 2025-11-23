import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.utils.RpcService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.url
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

class SyncedStateProvider() : StateProvider, ViewModel() {
    private var rpcService: RpcService? = null

    private var url: String? = null
    private var urlCheckJob: Job? = null
    private var dataSyncJob: Job? = null

    private val _habitState = MutableStateFlow(HabitState())
    override val habitState: StateFlow<HabitState> = _habitState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HabitState()
        )

    private val _taskState = MutableStateFlow(TaskState())
    override val taskState: StateFlow<TaskState> = _taskState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TaskState()
        )

    private val _isValidUrl = MutableStateFlow(true)
    val isValidUrl = _isValidUrl.asStateFlow()

    fun setUrl(passedUrl: String) {
        url = passedUrl
        rpcService = HttpClient(OkHttp) {
            installKrpc()
        }.rpc {
            url("ws://$passedUrl/rpc")
            rpcConfig {
                serialization {
                    json {
                        allowStructuredMapKeys = true
                    }
                }
            }
        }.withService<RpcService>()

        rpcService?.let { service ->
            dataSyncJob = viewModelScope.launch {
                combine(
                    service.getTaskData(),
                    service.getCompletedTasks()
                ) { tasks, completedTasks ->
                    _taskState.update {
                        it.copy(
                            tasks = tasks,
                            completedTasks = completedTasks,
                            currentCategory = if (it.currentCategory == null) {
                                tasks.keys.firstOrNull()
                            } else {
                                it.currentCategory
                            }
                        )
                    }
                }.launchIn(this)

                combine(
                    service.getHabitData(),
                    service.getCompletedHabits(),
                    service.overallAnalytics(),
                    service.startingDay(),
                    service.is24Hr()
                ) { habits, completedHabits, overallAnalytics, startingDay, is24Hr ->
                    _habitState.update {
                        it.copy(
                            habitsWithAnalytics = habits,
                            completedHabitIds = completedHabits,
                            overallAnalytics = overallAnalytics,
                            startingDay = startingDay,
                            is24Hr = is24Hr
                        )
                    }
                }.launchIn(this)

                service
                    .isUserSubscribed()
                    .onEach { pref ->
                        _habitState.update { it.copy(isUserSubscribed = pref) }
                    }.launchIn(this)
            }
        }
    }

    fun checkUrl(url: String) {
        urlCheckJob?.cancel()
        urlCheckJob = viewModelScope.launch {

        }
    }

    override fun onHabitAction(action: HabitsAction) {
        when (action) {
            is HabitsAction.AddHabit -> viewModelScope.launch {
                rpcService?.upsertHabit(action.habit)
            }

            is HabitsAction.DeleteHabit -> viewModelScope.launch {
                rpcService?.deleteHabit(action.habit.id)
            }

            HabitsAction.DismissAddHabitDialog -> _habitState.update { it.copy(showHabitAddSheet = false) }

            is HabitsAction.InsertStatus -> viewModelScope.launch {
                val isHabitCompleted =
                    _habitState.value.habitsWithAnalytics.find { it.habit == action.habit }?.statuses?.any { it.date == action.date }
                        ?: false

                if (isHabitCompleted) {

                    rpcService?.deleteHabitStatus(action.habit.id, action.date)

                } else {
                    rpcService?.insertHabitStatus(
                        HabitStatus(
                            habitId = action.habit.id,
                            date = action.date
                        )
                    )
                }
            }

            HabitsAction.OnAddHabitClicked -> {
                if (_habitState.value.isUserSubscribed || _habitState.value.habitsWithAnalytics.size <= 5) {
                    _habitState.update { it.copy(showHabitAddSheet = true) }
                }
            }

            HabitsAction.OnShowPaywall -> {}

            is HabitsAction.OnToggleCompactView -> _habitState.update { it.copy(compactHabitView = action.pref) }

            is HabitsAction.OnToggleEditState -> _habitState.update { it.copy(editState = action.pref) }

            is HabitsAction.OnTransientHabitReorder -> {
                val currentList = _habitState.value.habitsWithAnalytics.toMutableList()
                currentList.add(action.to, currentList.removeAt(action.from))
                _habitState.update { it.copy(habitsWithAnalytics = currentList) }
            }

            is HabitsAction.PrepareAnalytics -> _habitState.update { it.copy(analyticsHabitId = action.habit?.id) }

            HabitsAction.ReorderHabits -> viewModelScope.launch {
                val currentList = _habitState.value.habitsWithAnalytics.mapIndexed { index, analytics ->
                    analytics.habit.copy(index = index)
                }

                currentList.forEach { rpcService?.upsertHabit(it) }
            }

            is HabitsAction.UpdateHabit -> viewModelScope.launch {
                rpcService?.upsertHabit(action.habit)
            }
        }
    }

    override fun onTaskAction(action: TaskAction) {
        when (action) {
            is TaskAction.AddCategory -> viewModelScope.launch {
                rpcService?.upsertCategory(action.category)
            }

            is TaskAction.ChangeCategory -> _taskState.update { it.copy(currentCategory = action.category) }

            is TaskAction.DeleteCategory -> viewModelScope.launch {
                rpcService?.deleteCategory(action.category)
            }

            TaskAction.DeleteTasks -> viewModelScope.launch {
                _taskState.value.completedTasks.forEach {
                    rpcService?.deleteTask(it)
                }
            }

            is TaskAction.ReorderCategories -> viewModelScope.launch {
                action.mapping.forEach {
                    rpcService?.upsertCategory(it.second.copy(index = it.first))
                }

                delay(200)

                _taskState.update {
                    it.copy(currentCategory = it.tasks.keys.firstOrNull())
                }
            }

            is TaskAction.ReorderTasks -> viewModelScope.launch {
                action.mapping.forEach {
                    rpcService?.updateTaskIndexById(it.second.id, it.first)
                }
            }

            is TaskAction.UpsertTask -> viewModelScope.launch {
                rpcService?.upsertTask(action.task)
            }
        }
    }

}