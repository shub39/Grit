package com.shub39.grit.widgets.all_tasks_widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.CategoryColors
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.widgets.WidgetSize
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AllTasksWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<TaskRepo>()

        provideContent {
            val scope = rememberCoroutineScope()
            val size = LocalSize.current
            val tasks by repo.getTasksFlow().collectAsState(emptyMap())

            key(size) {
                GlanceTheme {
                    Content(
                        tasks = tasks.filter { it.value.isNotEmpty() },
                        onUpdateTaskStatus = {
                            scope.launch {
                                repo.upsertTask(it.copy(status = !it.status))
                            }
                        },
                        onUpdateWidget = {
                            scope.launch {
                                this@AllTasksWidget.updateAll(context)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
@GlanceComposable
private fun Content(
    tasks: Map<Category, List<Task>>,
    onUpdateTaskStatus: (Task) -> Unit,
    onUpdateWidget: () -> Unit
) {
    val size = LocalSize.current
    val context = LocalContext.current
    val roundedCornerSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .then(
                if (roundedCornerSupported) {
                    GlanceModifier
                        .background(GlanceTheme.colors.widgetBackground)
                        .cornerRadius(24.dp)
                }
                else {
                    GlanceModifier.background(
                        imageProvider = ImageProvider(R.drawable.rounded_4dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground)
                    )
                }
            )
            .clickable(actionStartActivity<MainActivity>())
    ) {
        TitleBar(
            startIcon = ImageProvider(R.drawable.check_list),
            title = context.getString(R.string.tasks),
            actions = {
                if (size.width >= WidgetSize.Width4) {
                    Box(GlanceModifier.padding(horizontal = 16.dp)) {
                        Image(
                            provider = ImageProvider(R.drawable.refresh),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                            modifier = GlanceModifier.clickable { onUpdateWidget() }
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = GlanceModifier
                .wrapContentSize()
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            items(tasks.entries.toList(), itemId = { it.key.id }) { taskGroup ->
                Column {
                    Text(
                        text = taskGroup.key.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                    Spacer(GlanceModifier.height(8.dp))
                    taskGroup.value.forEach { task ->
                        val status = task.status

                        Column {
                            Column(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .then(
                                        if (roundedCornerSupported) {
                                            GlanceModifier
                                                .cornerRadius(16.dp)
                                                .background(
                                                    if (!status) GlanceTheme.colors.secondaryContainer
                                                    else GlanceTheme.colors.tertiaryContainer
                                                )
                                        } else {
                                            GlanceModifier
                                                .background(
                                                    imageProvider = ImageProvider(R.drawable.rounded_16dp),
                                                    colorFilter = ColorFilter.tint(
                                                        if (!status) GlanceTheme.colors.secondaryContainer
                                                        else GlanceTheme.colors.tertiaryContainer
                                                    )
                                                )
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .clickable {
                                        onUpdateTaskStatus(task)
                                        onUpdateWidget()
                                    }
                            ) {
                                Text(
                                    text = task.title,
                                    modifier = GlanceModifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    style = TextStyle(
                                        color = if (!status) {
                                            GlanceTheme.colors.onSecondaryContainer
                                        } else GlanceTheme.colors.onTertiaryContainer,
                                        textDecoration = if (!status) {
                                            TextDecoration.None
                                        } else TextDecoration.LineThrough
                                    ),
                                    maxLines = 2
                                )
                            }
                            Spacer(GlanceModifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun GlancePreview() {
    Content(
        tasks = (0..3).associate {
            Category(
                id = it.toLong(),
                name = "Category $it",
                index = it,
                color = CategoryColors.GRAY.color
            ) to (0..10).map { taskId ->
                Task(
                    id = taskId.toLong(),
                    categoryId = it.toLong(),
                    title = "Task $taskId, Category $it",
                    index = it,
                    status = false,
                    reminder = null
                )
            }
        },
        onUpdateTaskStatus = { },
        onUpdateWidget = { },
    )
}