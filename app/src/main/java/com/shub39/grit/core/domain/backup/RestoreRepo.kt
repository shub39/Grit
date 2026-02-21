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
package com.shub39.grit.core.domain.backup

import android.net.Uri

interface RestoreRepo {
    suspend fun restoreData(uri: Uri): RestoreResult
}

sealed class RestoreResult {
    data object Success : RestoreResult()

    data class Failure(val exceptionType: RestoreFailedException) : RestoreResult()
}

enum class RestoreState {
    IDLE,
    RESTORING,
    RESTORED,
    FAILURE,
}

sealed interface RestoreFailedException {
    data object InvalidFile : RestoreFailedException

    data object OldSchema : RestoreFailedException
}

class SchemaMismatchException : Exception()
