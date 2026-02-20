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
package com.shub39.grit.core.data

import android.content.Context
import android.util.Log
import com.shub39.grit.core.domain.Changelog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class ChangelogManager(private val context: Context) {
    private val _changelogs: MutableStateFlow<Changelog> = MutableStateFlow(emptyList())
    val changelogs = _changelogs.asStateFlow().onStart { getChangelogs() }

    private fun getChangelogs() {
        try {
            val rawJson =
                context.assets.open("changelog.json").bufferedReader().use { it.readText() }

            val json = Json.decodeFromString<Changelog>(rawJson)

            _changelogs.update { json }
        } catch (e: Exception) {
            Log.e("ChangelogManager", "Error reading changelog", e)
        }
    }
}
