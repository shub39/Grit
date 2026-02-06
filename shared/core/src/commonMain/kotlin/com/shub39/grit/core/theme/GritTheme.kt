package com.shub39.grit.core.theme

import androidx.compose.runtime.Composable

@Composable
expect fun GritTheme(
    theme: Theme = Theme(),
    content: @Composable () -> Unit
)