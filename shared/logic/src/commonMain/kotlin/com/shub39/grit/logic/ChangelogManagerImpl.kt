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
package com.shub39.grit.logic

import com.shub39.grit.core.GritLogger
import com.shub39.grit.core.app.Changelog
import com.shub39.grit.core.interfaces.ChangelogManager
import grit.shared.logic.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [ChangelogManager::class])
class ChangelogManagerImpl : ChangelogManager {
    private val _changelogs: MutableStateFlow<Changelog> = MutableStateFlow(emptyList())
    override val changelogs: Flow<Changelog> = _changelogs.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch { getChangelogs() }
    }

    private suspend fun getChangelogs() {
        try {
            val rawJson = Res.readBytes("files/changelog.json").decodeToString()
            val json = Json.decodeFromString<Changelog>(rawJson)
            _changelogs.update { json }
        } catch (e: Exception) {
            GritLogger.e("ChangelogManagerImpl", "Unable to extract changelog", e)
        }
    }
}
