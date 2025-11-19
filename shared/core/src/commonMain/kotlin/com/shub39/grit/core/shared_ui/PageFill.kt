package com.shub39.grit.core.shared_ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// Wrapper to center everything
@Composable
fun  PageFill(
    modifier: Modifier = Modifier.fillMaxSize(),
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
        content = content
    )
}