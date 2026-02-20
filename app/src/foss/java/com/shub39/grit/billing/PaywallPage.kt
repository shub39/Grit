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
package com.shub39.grit.billing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.bmc
import grit.shared.core.generated.resources.buymeacoffee
import grit.shared.core.generated.resources.foss
import grit.shared.core.generated.resources.foss_desc
import grit.shared.core.generated.resources.warning
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallPage(isPlusUser: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    BackHandler { onDismissRequest() }

    Scaffold { paddingValues ->
        Box(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.warning),
                    contentDescription = "Warning",
                    modifier = Modifier.size(48.dp),
                )

                Text(
                    text = stringResource(Res.string.foss),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(text = stringResource(Res.string.foss_desc), textAlign = TextAlign.Center)

                Button(onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.buymeacoffee),
                            contentDescription = "Buy me a coffee",
                            modifier = Modifier.size(24.dp),
                        )

                        Text(text = stringResource(Res.string.bmc))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(
        theme = Theme(seedColor = Color.Red, appTheme = AppTheme.DARK, font = Fonts.FIGTREE)
    ) {
        PaywallPage(isPlusUser = false, onDismissRequest = {})
    }
}
