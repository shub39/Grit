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
package com.shub39.grit.widgets.common

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.shub39.grit.R

/** Rounded surface used by every widget */
@Composable
@GlanceComposable
fun GlanceModifier.widgetSurface(): GlanceModifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.background(GlanceTheme.colors.widgetBackground).cornerRadius(24.dp)
    } else {
        this.background(
            imageProvider = ImageProvider(R.drawable.rounded_4dp),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground),
        )
    }

/** Header of a group of connected list items */
@Composable
fun SectionHeader(text: String, modifier: GlanceModifier = GlanceModifier) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = 1,
        style = TextStyle(fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurfaceVariant),
    )
}

/** Placeholder for widgets without anything to show */
@Composable
fun NothingToShow() {
    Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Nothing to show", style = TextStyle(color = GlanceTheme.colors.onSurface))
    }
}

/** Trims the decimal part of whole numbers, 5.0 -> "5" and 2.5 -> "2.5" */
fun formatValue(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
