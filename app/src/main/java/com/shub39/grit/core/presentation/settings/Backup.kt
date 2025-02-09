package com.shub39.grit.core.presentation.settings

import androidx.compose.runtime.Composable
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.core.presentation.components.PageFill
import org.koin.compose.koinInject

@Composable
fun Backup(
    exportRepo: ExportRepo = koinInject(),
    restoreRepo: RestoreRepo = koinInject()
) = PageFill {

}