package com.shub39.grit.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.theme.Fonts.Companion.toFontRes

@Composable
actual fun GritTheme(
    theme: Theme,
    content: @Composable (() -> Unit)
)  {
    DynamicMaterialTheme(
        seedColor = theme.seedColor,
        isDark = when (theme.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.DARK -> true
            AppTheme.LIGHT -> false
        },
        isAmoled = theme.isAmoled,
        style = theme.paletteStyle,
        typography = theme.font.toFontRes()?.let { provideTypography(it) } ?: MaterialTheme.typography,
        content = content
    )
}