package com.shub39.grit.core.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.shub39.grit.core.domain.GritDatastore
import org.koin.compose.koinInject

@Composable
fun GritTheme(
    datastore: GritDatastore = koinInject(),
    content: @Composable () -> Unit
) {
    val isDark by datastore.getDarkThemePref().collectAsState(true)
    val isAmoled by datastore.getAmoledPref().collectAsState(false)
    val paletteStyle by datastore.getPaletteStyle().collectAsState(PaletteStyle.TonalSpot)
    val seedColor by datastore.getSeedColorFlow().collectAsState(Color.White.toArgb())
    val materialYou by datastore.getMaterialYouFlow().collectAsState(false)

    DynamicMaterialTheme(
        seedColor = if (materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            Color(seedColor)
        },
        useDarkTheme = when (isDark) {
            true -> true
            false -> false
            else -> isSystemInDarkTheme()
        },
        withAmoled = isAmoled,
        style = paletteStyle,
        typography = provideTypography(1f),
        content = content
    )
}