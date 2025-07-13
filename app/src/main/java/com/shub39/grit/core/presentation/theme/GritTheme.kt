package com.shub39.grit.core.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.shub39.grit.core.domain.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GritTheme(
    theme: Theme = Theme(),
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
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
        typography = provideTypography(1f, theme.font.resource),
        content = content
    )
}