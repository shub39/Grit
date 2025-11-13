package com.shub39.grit.core.data.backup.export

import android.os.Environment
import com.shub39.grit.core.data.backup.ExportSchema
import com.shub39.grit.core.data.backup.toCategorySchema
import com.shub39.grit.core.data.backup.toHabitSchema
import com.shub39.grit.core.data.backup.toHabitStatusSchema
import com.shub39.grit.core.data.backup.toTaskSchema
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.habits.domain.HabitRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportImpl(
    private val taskRepo: TaskRepo,
    private val habitsRepo: HabitRepo
): ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson() = coroutineScope {
        val habitsDef = async {
            withContext(Dispatchers.IO) {
                habitsRepo.getHabits().map { it.toHabitSchema() }
            }
        }.await()

        val statusesDef = async {
            withContext(Dispatchers.IO) {
                habitsRepo.getHabitStatuses().map { it.toHabitStatusSchema() }
            }
        }.await()

        val tasksDef = async {
            withContext(Dispatchers.IO) {
                taskRepo.getTasks().map { it.toTaskSchema() }
            }
        }.await()

        val categoriesDef = async {
            withContext(Dispatchers.IO) {
                taskRepo.getCategories().map { it.toCategorySchema() }
            }
        }.await()

        val exportFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Grit"
        )

        if (!exportFolder.exists() || !exportFolder.isDirectory) exportFolder.mkdirs()

        val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString().replace(":", "").replace(" ", "")
        val file = File(exportFolder, "Grit-Export-$time.json")

        file.writeText(
            Json.encodeToString(
                ExportSchema(
                    habits = habitsDef,
                    habitStatus = statusesDef,
                    tasks = tasksDef,
                    categories = categoriesDef
                )
            )
        )
    }
}