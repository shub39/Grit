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
package com.shub39.grit.core.data.backup.export

import android.os.Environment
import com.shub39.grit.core.data.backup.ExportSchema
import com.shub39.grit.core.data.backup.toCategorySchema
import com.shub39.grit.core.data.backup.toHabitSchema
import com.shub39.grit.core.data.backup.toHabitStatusSchema
import com.shub39.grit.core.data.backup.toTaskSchema
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.core.utils.now
import java.io.File
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [ExportRepo::class])
class ExportImpl(private val taskRepo: TaskRepo, private val habitsRepo: HabitRepo) : ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson() = coroutineScope {
        val habitsDef =
            async {
                    withContext(Dispatchers.IO) {
                        habitsRepo.getHabits().map { it.toHabitSchema() }
                    }
                }
                .await()

        val statusesDef =
            async {
                    withContext(Dispatchers.IO) {
                        habitsRepo.getHabitStatuses().map { it.toHabitStatusSchema() }
                    }
                }
                .await()

        val tasksDef =
            async { withContext(Dispatchers.IO) { taskRepo.getTasks().map { it.toTaskSchema() } } }
                .await()

        val categoriesDef =
            async {
                    withContext(Dispatchers.IO) {
                        taskRepo.getCategories().map { it.toCategorySchema() }
                    }
                }
                .await()

        val exportFolder =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Grit",
            )

        if (!exportFolder.exists() || !exportFolder.isDirectory) exportFolder.mkdirs()

        val time = LocalDateTime.now().toString().replace(":", "").replace(" ", "")
        val file = File(exportFolder, "Grit-Export-$time.json")

        file.writeText(
            Json.encodeToString(
                ExportSchema(
                    habits = habitsDef,
                    habitStatus = statusesDef,
                    tasks = tasksDef,
                    categories = categoriesDef,
                )
            )
        )
    }
}
