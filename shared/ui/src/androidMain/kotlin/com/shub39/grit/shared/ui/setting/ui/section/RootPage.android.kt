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
package com.shub39.grit.shared.ui.setting.ui.section

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

actual fun LazyListScope.languagePicker(onClick: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 33) {
        item {
            ListItem(
                colors = listItemColors(),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.language),
                        contentDescription = null,
                    )
                },
                headlineContent = { Text(text = stringResource(Res.string.language)) },
                supportingContent = {
                    Text(text = LocalLocale.current.platformLocale.displayLanguage)
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward),
                        contentDescription = "Navigate",
                    )
                },
                modifier = Modifier.clip(detachedItemShape()).clickable { onClick() },
            )
        }
    }
}
