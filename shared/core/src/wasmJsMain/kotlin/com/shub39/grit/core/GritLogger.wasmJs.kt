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
package com.shub39.grit.core

actual object GritLogger {
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        log("ERROR", tag, message, throwable)
    }

    actual fun d(tag: String, message: String) {
        log("DEBUG", tag, message, null)
    }

    actual fun i(tag: String, message: String) {
        log("INFO", tag, message, null)
    }

    private fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        val resolvedTag = tag.ifEmpty { "Grit" }
        val fullMessage = buildString {
            append("$level $resolvedTag: $message\n")
            if (throwable != null) {
                append(throwable.stackTraceToString())
                append('\n')
            }
        }

        println(fullMessage)
    }
}
