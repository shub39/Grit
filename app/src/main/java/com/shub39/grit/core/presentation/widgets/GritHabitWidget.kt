package com.shub39.grit.core.presentation.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.state.GlanceStateDefinition
import com.shub39.grit.habits.data.database.HabitDatabase
import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitStatusEntity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class GritWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GritHabitWidget()
}

class GritHabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HabitDatabase.getDatabase(context)
        val habits = database.habitDao().getAllHabits()

        val habit = habits.firstOrNull()
        val state = HabitWidgetState.getDataStore(context, id.toString()).data
        val habitStatuses = habit?.let { database.habitStatusDao().getStatusForHabit(it.id) }

        provideContent {
            GlanceTheme {
                HabitMapWidget(
                    habits = habits,
                    habitStatuses = habitStatuses
                )
            }
        }
    }

    @Composable
    private fun HabitMapWidget(
        habits: List<HabitEntity>,
        habitStatuses: List<HabitStatusEntity>?
    ) {

    }
}

object HabitWidgetState : GlanceStateDefinition<Long> {

    private const val DATASTORE_FILE_NAME_PREFIX = "habit_widget_id_"

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<Long> =
        DataStoreFactory.create(
            serializer = LongSerializer(defaultValue = 0L),
            produceFile = { getLocation(context, fileKey) }
        )

    override fun getLocation(context: Context, fileKey: String): File =
        context.dataStoreFile(DATASTORE_FILE_NAME_PREFIX + fileKey.lowercase())

}

private class LongSerializer(override val defaultValue: Long) : Serializer<Long> {
    override suspend fun readFrom(input: InputStream): Long {
        return try {
            DataInputStream(input).use { it.readLong() }
        } catch (e: IOException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: Long, output: OutputStream) {
        try {
            DataOutputStream(output).use { it.writeLong(t) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}