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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.theme.flexFontRounded
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.warning
import org.jetbrains.compose.resources.painterResource

@Composable
fun WarningDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    BasicAlertDialog(modifier = modifier, onDismissRequest = onDismissRequest) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialShapes.Pill.toShape(),
                            ),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.warning),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }

                Text(
                    text = "Important Warning",
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = flexFontEmphasis(),
                            color = MaterialTheme.colorScheme.error,
                        ),
                )
                Text(
                    text = "Your phone is about to stop being yours.",
                    style =
                        MaterialTheme.typography.titleMedium.copy(fontFamily = flexFontRounded()),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = WarningManager.getDaysLeft().toString(),
                        style =
                            MaterialTheme.typography.displayLarge.copy(
                                fontFamily = flexFontEmphasis(),
                                color = MaterialTheme.colorScheme.error,
                            ),
                    )
                    Text(text = "DAYS UNTIL LOCKDOWN", style = MaterialTheme.typography.titleMedium)
                }

                Text(
                    text =
                        "Starting September 2026, a silent update" +
                            " will block every Android app whose developer hasn't" +
                            " registered with Google, signed their contract, paid up," +
                            " and handed over government ID.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = "Every app and every device, worldwide, with no opt-out.",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                        ),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val uriHandler = LocalUriHandler.current
                    OutlinedButton(
                        onClick = { uriHandler.openUri("https://keepandroidopen.org/") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Do Something")
                    }

                    Button(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}
