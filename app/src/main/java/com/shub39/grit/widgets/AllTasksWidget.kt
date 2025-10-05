package com.shub39.grit.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.data.toCategory
import com.shub39.grit.core.data.toTask
import com.shub39.grit.core.data.toTaskEntity
import com.shub39.grit.core.domain.AlarmScheduler
import com.shub39.grit.tasks.data.database.CategoryDao
import com.shub39.grit.tasks.data.database.TasksDao
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

typealias GroupedTasks = List<Map.Entry<Category, List<Task>>>

class AllTasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AllTasksWidget()
}

class AllTasksWidgetRepository(
    private val context: Context,
    private val tasksDao: TasksDao,
    private val categoryDao: CategoryDao,
    private val scheduler: AlarmScheduler
) {
    suspend fun update() {
        AllTasksWidget().updateAll(context)
    }

    suspend fun updateTask(task: Task) {
        tasksDao.upsertTask(task.toTaskEntity())
        scheduler.schedule(task)
    }

    fun getGroupedTasks(): Flow<GroupedTasks> {
        val tasksFlow = tasksDao.getTasksFlow().map { entities ->
            entities.map { it.toTask() }.sortedBy { it.index }
        }
        val categoriesFlow = categoryDao.getCategoriesFlow().map { entities ->
            entities.map { it.toCategory() }.sortedBy { it.index }
        }

        return tasksFlow.combine(categoriesFlow) { tasks, categories ->
            categories.associateWith { category ->
                tasks.filter { it.categoryId == category.id }
            }.filter { it.value.isNotEmpty() }.map { it }
        }
    }
}

class AllTasksWidget : GlanceAppWidget(), KoinComponent {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<AllTasksWidgetRepository>()

        provideContent {
            val scope = rememberCoroutineScope()
            val tasks by repo.getGroupedTasks().collectAsState(initial = emptyList())

            GlanceTheme {
                TaskList(
                    context = context,
                    tasks = tasks,
                    onTaskStatusUpdate = { scope.launch {
                        repo.updateTask(it.copy(status = !it.status))
                    } }
                )
            }
        }
    }

    @Composable
    fun TaskList(
        context: Context,
        tasks: GroupedTasks,
        onTaskStatusUpdate: (Task) -> Unit
    ) {
        Scaffold(
            modifier = GlanceModifier.fillMaxSize(),
            backgroundColor = GlanceTheme.colors.widgetBackground,
            titleBar = {
                Row(
                    modifier = GlanceModifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = context.getString(R.string.tasks),
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                    )
                }
            }
        ) {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                items(tasks, itemId = { it.key.id }) { taskGroup ->
                    Column {
                        Text(
                            text = taskGroup.key.name,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(GlanceModifier.height(4.dp))
                        taskGroup.value.forEach { task ->
                            val status = task.status

                            Column {
                                Column(
                                    modifier = GlanceModifier
                                        .fillMaxWidth()
                                        .background(
                                            if (!status) GlanceTheme.colors.secondaryContainer
                                            else GlanceTheme.colors.tertiaryContainer
                                        )
                                        .cornerRadius(100.dp)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                        .clickable { onTaskStatusUpdate(task) }
                                ) {
                                    Text(
                                        text = task.title,
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        style = TextStyle(
                                            color = if (!status) GlanceTheme.colors.onSecondaryContainer
                                                    else GlanceTheme.colors.onTertiaryContainer,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (!status) TextDecoration.None
                                                             else TextDecoration.LineThrough
                                        ),
                                        maxLines = 2
                                    )
                                }
                                Spacer(GlanceModifier.height(4.dp))
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = context.getString(R.string.open_app),
                            modifier = GlanceModifier
                                .clickable(actionStartActivity<MainActivity>())
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}