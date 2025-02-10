package com.shub39.grit.core.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.core.domain.backup.RestoreResult
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.components.BetterIconButton
import com.shub39.grit.core.presentation.components.PageFill
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backup(
    exportRepo: ExportRepo = koinInject(),
    restoreRepo: RestoreRepo = koinInject()
) = PageFill {
    val coroutineScope = rememberCoroutineScope()

    var exportState by remember { mutableStateOf(ExportState.IDLE) }
    var restoreState by remember { mutableStateOf(RestoreState.IDLE) }

    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri = it }

    Column(
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.backup)
                )
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.export)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.export_desc)
                        )
                    },
                    trailingContent = {
                        BetterIconButton(
                            onClick = {
                                if (exportState == ExportState.IDLE) {
                                    coroutineScope.launch {
                                        exportState = ExportState.EXPORTING

                                        exportRepo.exportToJson()

                                        exportState = ExportState.EXPORTED
                                    }
                                }
                            }
                        ) {
                            when (exportState) {
                                ExportState.IDLE -> Icon(
                                    painter = painterResource(R.drawable.round_play_arrow_24),
                                    contentDescription = "Start"
                                )
                                ExportState.EXPORTING -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                ExportState.EXPORTED -> Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done"
                                )
                            }
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.restore)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.restore_desc)
                        )
                    },
                    trailingContent = {
                        if (uri == null) {
                            TextButton(
                                onClick = { launcher.launch(arrayOf("application/json")) }
                            ) {
                                Text(
                                    text = stringResource(R.string.choose_file)
                                )
                            }
                        } else {
                            BetterIconButton(
                                onClick = {
                                    if (restoreState == RestoreState.IDLE) {
                                        coroutineScope.launch {
                                            restoreState = RestoreState.RESTORING

                                            val result = restoreRepo.restoreSongs(uri!!)

                                            restoreState = if (result == RestoreResult.Success) {
                                                RestoreState.RESTORED
                                            } else {
                                                RestoreState.FAILURE
                                            }
                                        }
                                    }
                                }
                            ) {
                                when (restoreState) {
                                    RestoreState.IDLE -> Icon(
                                        painter = painterResource(R.drawable.round_play_arrow_24),
                                        contentDescription = "Start"
                                    )
                                    RestoreState.RESTORING -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    RestoreState.RESTORED -> Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Done"
                                    )
                                    RestoreState.FAILURE -> Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Fail"
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}