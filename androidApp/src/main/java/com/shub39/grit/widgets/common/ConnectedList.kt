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

import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import com.shub39.grit.R

/** Position of an item inside a connected (segmented) list */
enum class ListSlot {
    Single,
    Top,
    Middle,
    Bottom,
}

/** Resolves the slot of the item at [index] in a list of [count] items */
fun slotOf(index: Int, count: Int): ListSlot =
    when {
        count <= 1 -> ListSlot.Single
        index <= 0 -> ListSlot.Top
        index >= count - 1 -> ListSlot.Bottom
        else -> ListSlot.Middle
    }

/**
 * Background for an item of a connected list, mirroring the segmented shapes used in the app: 16dp
 * on the outer corners of the list and 4dp between items.
 */
@GlanceComposable
fun GlanceModifier.connectedBackground(slot: ListSlot, color: ColorProvider): GlanceModifier =
    this.background(
        imageProvider =
            ImageProvider(
                when (slot) {
                    ListSlot.Single -> R.drawable.widget_list_single
                    ListSlot.Top -> R.drawable.rounded_list_top
                    ListSlot.Middle -> R.drawable.widget_list_middle
                    ListSlot.Bottom -> R.drawable.widget_list_bottom
                }
            ),
        colorFilter = ColorFilter.tint(color),
    )
