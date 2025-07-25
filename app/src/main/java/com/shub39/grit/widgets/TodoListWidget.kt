package com.shub39.grit.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.LocalSize
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Column
import androidx.glance.layout.height
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.tasks.data.database.TasksDao
import com.shub39.grit.tasks.data.database.TaskEntity
import com.shub39.grit.tasks.data.database.TaskDatabase
import com.shub39.grit.tasks.data.database.TaskDbFactory
import com.shub39.grit.tasks.domain.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback

class TodoListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodoListWidget()
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            goAsync()
        }
    }
}

private val taskIdKey = ActionParameters.Key<Long>("taskId")

class ToggleTaskAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[taskIdKey] ?: return
        val db = TaskDbFactory(context).create().build()
        val repo = TodoListWidgetRepository(
            context = context,
            tasksDao = db.taskDao()
        )
        repo.toggleTaskStatus(taskId)
        // Force widget update
        TodoListWidget().update(context, glanceId)
    }
}

class TodoListWidgetRepository(
    private val context: Context,
    private val tasksDao: TasksDao
) {
    fun getTasks(): Flow<List<Task>> {
        return tasksDao.getTasksFlow()
            .map { entities ->
                entities
                    .filter { !it.status } // Only show uncompleted tasks
                    .map { entity ->
                        Task(
                            id = entity.id,
                            categoryId = entity.categoryId,
                            title = entity.title,
                            index = entity.index,
                            status = entity.status
                        )
                    }
            }
    }
    
    suspend fun toggleTaskStatus(taskId: Long) {
        val tasks = tasksDao.getTasks()
        val task = tasks.find { it.id == taskId }
        task?.let {
            tasksDao.upsertTask(it.copy(status = !it.status))
        }
    }

    suspend fun update() {
        TodoListWidget().updateAll(context)
    }
}

class TodoListWidget : GlanceAppWidget(), KoinComponent {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<TodoListWidgetRepository>()
        
        provideContent {
            val tasks by repo.getTasks().collectAsState(emptyList())
            
            GlanceTheme {
                TodoListContent(
                    context = context,
                    tasks = tasks
                )
            }
        }
    }
    
    @Composable
    fun TodoListContent(
        context: Context,
        tasks: List<Task>
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
                        text = context.getString(R.string.todo_list),
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                    )
                    
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    
                    Text(
                        text = "${tasks.size}",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        ) {
            if (tasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = context.getString(R.string.add),
                        modifier = GlanceModifier
                            .clickable(actionStartActivity<MainActivity>())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 20.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(tasks) { task ->
                        TaskItem(task = task)
                    }
                    
                    item {
                        Spacer(modifier = GlanceModifier.height(12.dp))
                    }
                }
            }
        }
    }
    
    @Composable
    fun TaskItem(task: Task) {
        Column {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .background(GlanceTheme.colors.secondaryContainer)
                    .cornerRadius(10.dp)
                    .padding(4.dp)
                    .clickable(
                        actionRunCallback<ToggleTaskAction>(
                            parameters = actionParametersOf(taskIdKey to task.id)
                        )
                    )
            ) {
                Text(
                    text = task.title,
                    modifier = GlanceModifier.fillMaxWidth(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.height(4.dp))
        }
    }
}