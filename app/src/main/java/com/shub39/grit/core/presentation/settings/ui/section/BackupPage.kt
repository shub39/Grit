package com.shub39.grit.core.presentation.settings.ui.section

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.endItemShape
import com.shub39.grit.core.presentation.settings.ui.component.leadingItemShape
import com.shub39.grit.core.presentation.settings.ui.component.listItemColors
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.backup
import grit.shared.core.generated.resources.check_circle
import grit.shared.core.generated.resources.choose_file
import grit.shared.core.generated.resources.download
import grit.shared.core.generated.resources.drive_folder_upload
import grit.shared.core.generated.resources.export
import grit.shared.core.generated.resources.export_desc
import grit.shared.core.generated.resources.play_arrow
import grit.shared.core.generated.resources.restore
import grit.shared.core.generated.resources.restore_desc
import grit.shared.core.generated.resources.warning
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(SettingsAction.OnResetBackupState)
    }

    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri = it }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.backup)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 60.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(leadingItemShape())
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.export)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.drive_folder_upload),
                                    contentDescription = null
                                )
                            },
                            colors = listItemColors(),
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.export_desc)
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            Button(
                                onClick = { onAction(SettingsAction.OnExport) },
                                enabled = state.backupState.exportState == ExportState.IDLE,
                                modifier = Modifier.weight(1f)
                            ) {
                                when (state.backupState.exportState) {
                                    ExportState.IDLE -> Icon(
                                        painter = painterResource(Res.drawable.play_arrow),
                                        contentDescription = "Start"
                                    )

                                    ExportState.EXPORTING -> CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )

                                    ExportState.EXPORTED -> Icon(
                                        imageVector = vectorResource(Res.drawable.check_circle),
                                        contentDescription = "Done"
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.clip(endItemShape())
                    ) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.download),
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.restore)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.restore_desc)
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            if (uri == null) {
                                Button(
                                    onClick = { launcher.launch(arrayOf("application/json")) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.choose_file)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onAction(SettingsAction.OnRestore(uri!!)) },
                                    enabled = state.backupState.restoreState == RestoreState.IDLE,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    when (state.backupState.restoreState) {
                                        RestoreState.IDLE -> Icon(
                                            painter = painterResource(Res.drawable.play_arrow),
                                            contentDescription = "Start"
                                        )

                                        RestoreState.RESTORING -> CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )

                                        RestoreState.RESTORED -> Icon(
                                            imageVector = vectorResource(Res.drawable.check_circle),
                                            contentDescription = "Done"
                                        )

                                        RestoreState.FAILURE -> Icon(
                                            imageVector = vectorResource(Res.drawable.warning),
                                            contentDescription = "Fail"
                                        )
                                    }
                                }
                            }
                        }
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
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Surface {
            BackupPage(
                state = SettingsState(),
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}