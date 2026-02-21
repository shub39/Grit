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
package com.shub39.grit.widgets.habitoverviewwidget

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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.utils.now
import com.shub39.grit.widgets.WidgetSize
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HabitOverviewWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = get<HabitRepo>()

        provideContent {
            val size = LocalSize.current
            val scope = rememberCoroutineScope()
            val habits by repo.getHabitsWithStatus().collectAsState(initial = emptyList())

            key(size) {
                GlanceTheme {
                    Content(
                        habitsWithStatus = habits,
                        onUpdateHabit = { habitWithStatus ->
                            scope.launch {
                                if (habitWithStatus.second) {
                                    repo.deleteHabitStatus(
                                        habitId = habitWithStatus.first.id,
                                        date = LocalDate.now(),
                                    )
                                } else {
                                    repo.insertHabitStatus(
                                        habitStatus =
                                            HabitStatus(
                                                habitId = habitWithStatus.first.id,
                                                date = LocalDate.now(),
                                            )
                                    )
                                }
                            }
                        },
                        onUpdateWidget = {
                            scope.launch { this@HabitOverviewWidget.updateAll(context) }
                        },
                    )
                }
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val previewItems =
            listOf(
                Habit(
                    id = 1,
                    title = "Read a Book",
                    description = "20 pages at least",
                    time = LocalDateTime.now(),
                    days = emptySet(),
                    index = 1,
                    reminder = false,
                ) to true,
                Habit(
                    id = 2,
                    title = "Exercise",
                    description = "40 Minutes daily",
                    time = LocalDateTime.now(),
                    days = setOf(),
                    index = 2,
                    reminder = false,
                ) to false,
            )

        provideContent {
            Content(habitsWithStatus = previewItems, onUpdateHabit = {}, onUpdateWidget = {})
        }
    }
}

@GlanceComposable
@Composable
private fun Content(
    habitsWithStatus: List<Pair<Habit, Boolean>>,
    onUpdateHabit: (Pair<Habit, Boolean>) -> Unit,
    onUpdateWidget: () -> Unit,
    modifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val roundedCornerSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .then(
                    if (roundedCornerSupported) {
                        GlanceModifier.background(GlanceTheme.colors.widgetBackground)
                            .cornerRadius(24.dp)
                    } else {
                        GlanceModifier.background(
                            imageProvider = ImageProvider(R.drawable.rounded_4dp),
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground),
                        )
                    }
                )
                .clickable(actionStartActivity<MainActivity>())
    ) {
        TitleBar(
            startIcon = ImageProvider(R.drawable.alarm),
            title = context.getString(R.string.habits),
            actions = {
                Text(
                    text = "${habitsWithStatus.count { it.second }}/${habitsWithStatus.size}",
                    style = TextStyle(color = GlanceTheme.colors.onSurface),
                )

                if (size.width >= WidgetSize.Width4) {
                    Box(GlanceModifier.padding(horizontal = 16.dp)) {
                        Image(
                            provider = ImageProvider(R.drawable.refresh),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                            modifier = GlanceModifier.clickable { onUpdateWidget() },
                        )
                    }
                } else {
                    Spacer(modifier = GlanceModifier.width(16.dp))
                }
            },
        )

        LazyColumn(
            modifier = GlanceModifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            items(habitsWithStatus, itemId = { it.first.id }) { habitWithStatus ->
                Column {
                    Row(
                        modifier =
                            GlanceModifier.fillMaxWidth()
                                .then(
                                    if (roundedCornerSupported) {
                                        GlanceModifier.cornerRadius(16.dp)
                                            .background(
                                                if (!habitWithStatus.second)
                                                    GlanceTheme.colors.secondaryContainer
                                                else GlanceTheme.colors.tertiaryContainer
                                            )
                                    } else {
                                        GlanceModifier.background(
                                            imageProvider = ImageProvider(R.drawable.rounded_16dp),
                                            colorFilter =
                                                ColorFilter.tint(
                                                    if (!habitWithStatus.second)
                                                        GlanceTheme.colors.secondaryContainer
                                                    else GlanceTheme.colors.tertiaryContainer
                                                ),
                                        )
                                    }
                                )
                                .padding(vertical = 8.dp)
                                .clickable {
                                    onUpdateHabit(habitWithStatus)
                                    onUpdateWidget()
                                },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (size.width >= WidgetSize.Width4) {
                            Image(
                                provider =
                                    ImageProvider(
                                        if (habitWithStatus.second) {
                                            R.drawable.check_circle
                                        } else R.drawable.circle_border
                                    ),
                                contentDescription = null,
                                modifier = GlanceModifier.padding(start = 12.dp),
                                colorFilter =
                                    ColorFilter.tint(
                                        if (!habitWithStatus.second) {
                                            GlanceTheme.colors.onSecondaryContainer
                                        } else GlanceTheme.colors.onTertiaryContainer
                                    ),
                            )
                        }

                        Column(modifier = GlanceModifier.defaultWeight().padding(8.dp)) {
                            Text(
                                text = habitWithStatus.first.title,
                                style =
                                    TextStyle(
                                        color =
                                            if (!habitWithStatus.second) {
                                                GlanceTheme.colors.onSecondaryContainer
                                            } else GlanceTheme.colors.onTertiaryContainer,
                                        textDecoration =
                                            if (!habitWithStatus.second) {
                                                TextDecoration.None
                                            } else TextDecoration.LineThrough,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                maxLines = 1,
                            )

                            if (habitWithStatus.first.description.isNotEmpty()) {
                                Text(
                                    text = habitWithStatus.first.description,
                                    style =
                                        TextStyle(
                                            color =
                                                if (!habitWithStatus.second) {
                                                    GlanceTheme.colors.onSecondaryContainer
                                                } else GlanceTheme.colors.onTertiaryContainer,
                                            textDecoration =
                                                if (!habitWithStatus.second) {
                                                    TextDecoration.None
                                                } else TextDecoration.LineThrough,
                                        ),
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }

            item { Spacer(modifier = GlanceModifier.height(4.dp)) }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun GlancePreview() {
    Content(
        habitsWithStatus =
            (0..10).map {
                Habit(
                    id = it.toLong(),
                    title = "Habit $it",
                    description = "Habit Description $it",
                    time = LocalDateTime.now(),
                    days = emptySet(),
                    index = it,
                    reminder = false,
                ) to (it % 2 == 0)
            },
        onUpdateHabit = {},
        onUpdateWidget = {},
    )
}
