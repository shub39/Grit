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
package com.shub39.grit.warning

import com.shub39.grit.BuildConfig
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

object WarningManager {
    fun showWarning(): Boolean = (BuildConfig.FLAVOR == "foss") && (getDaysLeft() >= 0)

    private val _showWarningDialog = MutableStateFlow(showWarning())

    val showWarningDialog = _showWarningDialog.asStateFlow()

    fun updateWarningDialog(newValue: Boolean) {
        _showWarningDialog.update { newValue }
    }

    fun getDaysLeft(): Int {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .daysUntil(LocalDate(year = 2026, month = 9, day = 1))
    }
}
