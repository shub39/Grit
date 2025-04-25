package com.shub39.grit.core.data.backup.restore

import android.content.Context
import android.net.Uri
import com.shub39.grit.core.data.backup.ExportSchema
import com.shub39.grit.core.data.backup.toCategory
import com.shub39.grit.core.data.backup.toHabit
import com.shub39.grit.core.data.backup.toHabitStatus
import com.shub39.grit.core.data.backup.toTask
import com.shub39.grit.core.domain.backup.RestoreFailedException
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.core.domain.backup.RestoreResult
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.tasks.domain.TaskRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class RestoreImpl(
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val context: Context
): RestoreRepo {
    override suspend fun restoreData(uri: Uri): RestoreResult {
        return try {
            val file = kotlin.io.path.createTempFile()

            context.contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            val json = Json {
                ignoreUnknownKeys = true
            }

            val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readText())

            taskRepo.deleteAllTasks()
            taskRepo.deleteAllCategories()
            habitRepo.deleteAllHabits()
            habitRepo.deleteAllHabitStatus()

            withContext(Dispatchers.IO) {
                awaitAll(
                    async {
                        jsonDeserialized.habits.map { it.toHabit() }.forEach {
                            habitRepo.upsertHabit(it)
                        }
                    },
                    async {
                        jsonDeserialized.habitStatus.map { it.toHabitStatus() }.forEach {
                            habitRepo.insertHabitStatus(it)
                        }
                    },
                    async {
                        jsonDeserialized.tasks.map { it.toTask() }.forEach {
                            taskRepo.upsertTask(it)
                        }
                    },
                    async {
                        jsonDeserialized.categories.map { it.toCategory() }.forEach {
                            taskRepo.upsertCategory(it)
                        }
                    }
                )
            }

            RestoreResult.Success
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            RestoreResult.Failure(RestoreFailedException.InvalidFile)
        } catch (e: SerializationException) {
            e.printStackTrace()
            RestoreResult.Failure(RestoreFailedException.OldSchema)
        }
    }
}