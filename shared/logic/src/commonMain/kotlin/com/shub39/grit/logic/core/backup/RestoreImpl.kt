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
package com.shub39.grit.logic.core.backup

import com.shub39.grit.core.GritLogger
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.interfaces.AlarmScheduler
import com.shub39.grit.core.settings.backup.RestoreRepo
import com.shub39.grit.core.settings.backup.RestoreResult
import com.shub39.grit.core.settings.backup.SchemaMismatchException
import com.shub39.grit.core.tasks.TaskRepo
import com.shub39.grit.logic.habits.database.HabitDatabase
import com.shub39.grit.logic.tasks.database.TaskDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [RestoreRepo::class])
class RestoreImpl(
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val alarmScheduler: AlarmScheduler,
) : RestoreRepo {
    override suspend fun restoreData(): RestoreResult {
        return try {
            val file =
                FileKit.openFilePicker(mode = FileKitMode.Single, type = FileKitType.File("json"))
                    ?: return RestoreResult.Failure(exceptionType = InvalidFile)

            val json = Json { ignoreUnknownKeys = true }

            val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readString())

            if (
                jsonDeserialized.tasksSchemaVersion != TaskDatabase.SCHEMA_VERSION ||
                    jsonDeserialized.habitsSchemaVersion != HabitDatabase.SCHEMA_VERSION
            ) {
                throw SchemaMismatchException()
            }

            withContext(Dispatchers.IO) {
                awaitAll(
                    async {
                        habitRepo.getHabits().forEach { alarmScheduler.cancel(it) }

                        jsonDeserialized.habits
                            .map { it.toHabit() }
                            .forEach {
                                habitRepo.upsertHabit(it)
                                alarmScheduler.schedule(it)
                            }

                        jsonDeserialized.habitStatus
                            .map { it.toHabitStatus() }
                            .forEach { habitRepo.insertHabitStatus(it) }
                    },
                    async {
                        taskRepo.getTasks().forEach { alarmScheduler.cancel(it) }
                        taskRepo.getSubTasks().forEach { alarmScheduler.cancel(it) }

                        jsonDeserialized.categories
                            .map { it.toCategory() }
                            .forEach { taskRepo.upsertCategory(it) }

                        jsonDeserialized.tasks
                            .map { it.toTask() }
                            .forEach {
                                taskRepo.upsertTask(it)
                                alarmScheduler.schedule(it)
                            }

                        jsonDeserialized.subTasks
                            .map { it.toSubTask() }
                            .forEach {
                                taskRepo.upsertSubTask(it)
                                alarmScheduler.schedule(it)
                            }
                    },
                )
            }

            RestoreResult.Success
        } catch (e: SchemaMismatchException) {
            GritLogger.e("RestoreRepo", "Failed to restore data, old schema: ", e)
            RestoreResult.Failure(OldSchema)
        } catch (e: SerializationException) {
            GritLogger.e("RestoreRepo", "Failed to deserialize, invalid file: ", e)
            RestoreResult.Failure(InvalidFile)
        }
    }
}
