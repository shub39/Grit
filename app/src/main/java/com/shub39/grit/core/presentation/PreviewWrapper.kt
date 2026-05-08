package com.shub39.grit.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme

class GritPreviewWrapper : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        GritTheme(
            theme = Theme()
        ) {
            content()
        }
    }
}