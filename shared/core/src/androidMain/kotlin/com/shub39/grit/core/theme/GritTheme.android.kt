package com.shub39.grit.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.theme.Fonts.Companion.toFontRes

@Composable
actual fun GritTheme(
    theme: Theme,
    content: @Composable (() -> Unit)
)  {
    DynamicMaterialTheme(
        seedColor = if (theme.isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            theme.seedColor
        },
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