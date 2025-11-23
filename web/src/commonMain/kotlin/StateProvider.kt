import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import kotlinx.coroutines.flow.StateFlow

interface StateProvider {
    val habitState: StateFlow<HabitState>
    val taskState: StateFlow<TaskState>

    fun onHabitAction(action: HabitsAction)
    fun onTaskAction(action: TaskAction)
}