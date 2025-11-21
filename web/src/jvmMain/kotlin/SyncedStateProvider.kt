import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.utils.StateData
import com.shub39.grit.core.utils.SuccessResponse
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncedStateProvider() : StateProvider, ViewModel() {
    private val client = createHttpClent()

    private var url: String? = null
    private var urlCheckJob: Job? = null

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

    private val _isValidUrl = MutableStateFlow(false)
    val isValidUrl = _isValidUrl.asStateFlow()

    fun setUrl(passedUrl: String) = viewModelScope.launch {
        url = passedUrl
        getData()
    }

    fun checkUrl(url: String) {
        urlCheckJob?.cancel()
        urlCheckJob = viewModelScope.launch {
            val response = safeCall<SuccessResponse> {
                client.get(urlString = "http://$url/api")
            }

            when (response) {
                is Result.Error -> _isValidUrl.update { false }
                is Result.Success -> _isValidUrl.update { true }
            }
        }
    }

    override fun onHabitAction(action: HabitsAction) {
        when (action) {
            is HabitsAction.AddHabit -> {}
            is HabitsAction.DeleteHabit -> {}
            HabitsAction.DismissAddHabitDialog -> _habitState.update { it.copy(showHabitAddSheet = false) }
            is HabitsAction.InsertStatus -> viewModelScope.launch {
                if (url == null) return@launch

                client.post(
                    urlString = "http://$url/api/habit/status"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(Pair(action.habit, action.date))
                }

                getData()
            }
            HabitsAction.OnAddHabitClicked -> _habitState.update { it.copy(showHabitAddSheet = true) }
            HabitsAction.OnShowPaywall -> {}
            is HabitsAction.OnToggleCompactView -> _habitState.update { it.copy(compactHabitView = action.pref) }
            is HabitsAction.OnToggleEditState -> _habitState.update { it.copy(editState = action.pref) }
            is HabitsAction.OnTransientHabitReorder -> {
                val currentList = _habitState.value.habitsWithAnalytics.toMutableList()
                currentList.add(action.to, currentList.removeAt(action.from))
                _habitState.update { it.copy(habitsWithAnalytics = currentList) }
            }

            is HabitsAction.PrepareAnalytics -> _habitState.update { it.copy(analyticsHabitId = action.habit?.id) }
            HabitsAction.ReorderHabits -> {}
            is HabitsAction.UpdateHabit -> {}
        }
    }

    override fun onTaskAction(action: TaskAction) {
        when (action) {
            is TaskAction.AddCategory -> {}
            is TaskAction.ChangeCategory -> _taskState.update { it.copy(currentCategory = action.category) }
            is TaskAction.DeleteCategory -> {}
            TaskAction.DeleteTasks -> {}
            is TaskAction.ReorderCategories -> {}
            is TaskAction.ReorderTasks -> {}
            is TaskAction.UpsertTask -> {}
        }
    }

    override fun onRefresh() {
        viewModelScope.launch { getData() }
    }

    private suspend fun getData() {
        if (url == null) return

        val response = safeCall<StateData> {
            client.get(urlString = "http://$url/api/data")
        }

        when (response) {
            is Result.Success -> {
                _taskState.update {
                    it.copy(
                        tasks = response.data.taskData,
                        completedTasks = response.data.completedTasks,
                        is24Hour = response.data.is24Hr
                    )
                }

                _habitState.update {
                    it.copy(
                        habitsWithAnalytics = response.data.habitData,
                        completedHabitIds = response.data.completedHabitIds,
                        overallAnalytics = response.data.overallAnalytics,
                        startingDay = response.data.startingDay,
                        isUserSubscribed = response.data.isUserSubscribed,
                        is24Hr = response.data.is24Hr
                    )
                }
            }

            else -> {}
        }
    }

}