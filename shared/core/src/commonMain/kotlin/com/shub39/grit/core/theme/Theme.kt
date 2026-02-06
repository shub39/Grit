package com.shub39.grit.core.theme

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle

data class Theme(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAmoled: Boolean = false,
    val isMaterialYou: Boolean = false,
    val font: Fonts = Fonts.FIGTREE,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val seedColor: Color = Color.White
)