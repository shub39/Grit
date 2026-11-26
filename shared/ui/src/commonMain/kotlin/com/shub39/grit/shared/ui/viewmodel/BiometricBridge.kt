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
package com.shub39.grit.shared.ui.viewmodel

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object BiometricBridge {
    private val _requests = MutableSharedFlow<CompletableDeferred<Boolean>>(extraBufferCapacity = 1)
    val requests: SharedFlow<CompletableDeferred<Boolean>> = _requests.asSharedFlow()
    suspend fun requestAuthentication(): Boolean {
        if (_requests.subscriptionCount.value == 0) return true
        val result = CompletableDeferred<Boolean>()
        _requests.emit(result)
        return result.await()
    }
}