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

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask

/**
 * Directory used for the Room databases and the preference DataStore.
 *
 * `Application Support` is the iOS equivalent of Android's `filesDir` / desktop's app data dir, and
 * unlike `Documents` it is not exposed to the Files app. It is backed up by iCloud unless excluded,
 * which matches the Android behaviour of keeping the databases in the app sandbox.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun appSupportDirectoryPath(): String {
    val url =
        NSFileManager.defaultManager.URLForDirectory(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            null,
            true,
            null,
        )

    return url?.path ?: NSTemporaryDirectory()
}
