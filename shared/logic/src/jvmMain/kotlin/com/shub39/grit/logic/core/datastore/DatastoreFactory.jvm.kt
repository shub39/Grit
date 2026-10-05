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
package com.shub39.grit.logic.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File
import org.koin.core.annotation.Single

@Single
actual class DatastoreFactory {
    private val os = System.getProperty("os.name").lowercase()
    private val userHome = System.getProperty("user.home")
    private val appDataDir =
        when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Rush")
            os.contains("mac") -> File(userHome, "Library/Application Support/Rush")
            else -> File(userHome, ".local/share/Rush")
        }

    init {
        if (!appDataDir.exists()) appDataDir.mkdirs()
    }

    actual fun getPreferencesDataStore(): DataStore<Preferences> {
        val storeFile = File(appDataDir, DATA_STORE_FILE_NAME)
        return createDataStore(producePath = { storeFile.absolutePath })
    }
}
