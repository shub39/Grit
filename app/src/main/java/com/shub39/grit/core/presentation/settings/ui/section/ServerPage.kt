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
package com.shub39.grit.core.presentation.settings.ui.section

// import android.Manifest
// import android.content.pm.PackageManager
// import android.os.Build
// import android.widget.Toast
// import androidx.activity.compose.rememberLauncherForActivityResult
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.compose.animation.AnimatedVisibility
// import androidx.compose.foundation.layout.Arrangement
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.PaddingValues
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.height
// import androidx.compose.foundation.layout.size
// import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.automirrored.filled.ArrowBack
// import androidx.compose.material.icons.rounded.CopyAll
// import androidx.compose.material.icons.rounded.InstallDesktop
// import androidx.compose.material.icons.rounded.PowerSettingsNew
// import androidx.compose.material.icons.rounded.Wifi
// import androidx.compose.material3.Button
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.Icon
// import androidx.compose.material3.IconButton
// import androidx.compose.material3.ListItem
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Switch
// import androidx.compose.material3.Text
// import androidx.compose.material3.TopAppBar
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.rememberCoroutineScope
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.platform.LocalClipboard
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.platform.LocalUriHandler
// import androidx.compose.ui.text.style.TextAlign
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import androidx.core.content.ContextCompat
// import com.shub39.grit.core.theme.AppTheme
// import com.shub39.grit.core.presentation.copyToClipboard
// import com.shub39.grit.core.presentation.settings.ServerState
// import com.shub39.grit.core.presentation.settings.SettingsState
// import com.shub39.grit.core.presentation.settings.ui.component.detachedItemShape
// import com.shub39.grit.core.presentation.settings.ui.component.endItemShape
// import com.shub39.grit.core.presentation.settings.ui.component.leadingItemShape
// import com.shub39.grit.core.presentation.settings.ui.component.listItemColors
// import com.shub39.grit.core.theme.GritTheme
// import com.shub39.grit.core.theme.Theme
// import com.shub39.grit.server.GritServerService
// import grit.shared.core.generated.resources.Res
// import grit.shared.core.generated.resources.download
// import grit.shared.core.generated.resources.grit_desktop
// import grit.shared.core.generated.resources.grit_desktop_desc
// import grit.shared.core.generated.resources.server
// import grit.shared.core.generated.resources.server_running
// import grit.shared.core.generated.resources.start_server
// import kotlinx.coroutines.launch
// import org.jetbrains.compose.resources.stringResource
//
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun ServerPage(
//    state: SettingsState,
//    onNavigateBack: () -> Unit
// ) {
//    val context = LocalContext.current
//    val uriHandler = LocalUriHandler.current
//    val clipBoardManager = LocalClipboard.current
//    val scope = rememberCoroutineScope()
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        if (granted) {
//            GritServerService.startService(context, state.serverState.serverPort)
//        } else Toast.makeText(context, "Notification permission denied",
// Toast.LENGTH_SHORT).show()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        TopAppBar(
//            title = {
//                Text(
//                    text = stringResource(Res.string.server)
//                )
//            },
//            navigationIcon = {
//                IconButton(
//                    onClick = onNavigateBack
//                ) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
//                        contentDescription = "Navigate Back"
//                    )
//                }
//            }
//        )
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 32.dp, bottom =
// 60.dp),
//        ) {
//            item {
//                Column(
//                    modifier = Modifier.fillParentMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.InstallDesktop,
//                        contentDescription = null,
//                        modifier = Modifier.size(100.dp)
//                    )
//
//                    Text(
//                        text = stringResource(Res.string.grit_desktop),
//                        style = MaterialTheme.typography.headlineMedium.copy(
//                            textAlign = TextAlign.Center
//                        )
//                    )
//
//                    Text(
//                        text = stringResource(Res.string.grit_desktop_desc),
//                        style = MaterialTheme.typography.bodyLarge.copy(
//                            textAlign = TextAlign.Center
//                        )
//                    )
//
//                    Button(
//                        onClick = {
//                            // TODO: Replace with link to download
////                            uriHandler.openUri()
//                        }
//                    ) {
//                        Text(
//                            text = stringResource(Res.string.download)
//                        )
//                    }
//                }
//            }
//
//            item { Spacer(modifier = Modifier.height(32.dp)) }
//
//            item {
//                Column(
//                    modifier = Modifier.fillParentMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(2.dp)
//                ) {
//                    ListItem(
//                        colors = listItemColors(),
//                        modifier = Modifier.clip(
//                            if (state.serverState.isRunning) {
//                                leadingItemShape()
//                            } else {
//                                detachedItemShape()
//                            }
//                        ),
//                        leadingContent = {
//                            Icon(
//                                imageVector = if (state.serverState.isRunning) {
//                                    Icons.Rounded.Wifi
//                                } else {
//                                    Icons.Rounded.PowerSettingsNew
//                                },
//                                contentDescription = null
//                            )
//                        },
//                        headlineContent = {
//                            Text(
//                                text = stringResource(
//                                    if (state.serverState.isRunning) {
//                                        Res.string.server_running
//                                    } else {
//                                        Res.string.start_server
//                                    }
//                                )
//                            )
//                        },
//                        trailingContent = {
//                            Switch(
//                                checked = state.serverState.isRunning,
//                                onCheckedChange = {
//                                    if (!state.serverState.isRunning) {
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
// &&
//                                            ContextCompat.checkSelfPermission(
//                                                context,
//                                                Manifest.permission.POST_NOTIFICATIONS
//                                            ) != PackageManager.PERMISSION_GRANTED
//                                        ) {
//
// launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                                        } else {
//                                            GritServerService.startService(
//                                                context,
//                                                state.serverState.serverPort
//                                            )
//                                        }
//                                    } else {
//                                        GritServerService.stopService(context)
//                                    }
//                                }
//                            )
//                        }
//                    )
//
//                    AnimatedVisibility(visible = state.serverState.isRunning &&
// state.serverState.serverUrl != null) {
//                        ListItem(
//                            headlineContent = {
//                                Text(
//                                    text = state.serverState.serverUrl ?: "..."
//                                )
//                            },
//                            trailingContent = {
//                                IconButton(
//                                    onClick = {
//                                        scope.launch {
//
// clipBoardManager.copyToClipboard(state.serverState.serverUrl ?: "...")
//                                        }
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Rounded.CopyAll,
//                                        contentDescription = null
//                                    )
//                                }
//                            },
//                            colors = listItemColors(),
//                            modifier = Modifier.clip(endItemShape())
//                        )
//                    }
//                }
//            }
//        }
//    }
// }
//
// @Preview
// @Composable
// private fun Preview() {
//    GritTheme(
//        theme = Theme(appTheme = AppTheme.DARK)
//    ) {
//        Surface {
//            ServerPage(
//                state = SettingsState(
//                    serverState = ServerState(
//                        isRunning = true,
//                        serverUrl = "192.168"
//                    )
//                ),
//                onNavigateBack = {}
//            )
//        }
//    }
// }
