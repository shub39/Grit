package com.shub39.grit.core.theme


import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.poppins_regular
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

val TYPOGRAPHY = Typography()

@Composable
fun provideTypography(
    font: FontResource = Res.font.poppins_regular
): Typography {
    val selectedFont = FontFamily(Font(font))

    return Typography(
        displayLarge = TYPOGRAPHY.displayLarge.copy(fontFamily = selectedFont),
        displayMedium = TYPOGRAPHY.displayMedium.copy(fontFamily = selectedFont),
        displaySmall = TYPOGRAPHY.displaySmall.copy(fontFamily = selectedFont),
        headlineLarge = TYPOGRAPHY.headlineLarge.copy(fontFamily = selectedFont),
        headlineMedium = TYPOGRAPHY.headlineMedium.copy(fontFamily = selectedFont),
        headlineSmall = TYPOGRAPHY.headlineSmall.copy(fontFamily = selectedFont),
        titleLarge = TYPOGRAPHY.titleLarge.copy(fontFamily = selectedFont),
        titleMedium = TYPOGRAPHY.titleMedium.copy(fontFamily = selectedFont),
        titleSmall = TYPOGRAPHY.titleSmall.copy(fontFamily = selectedFont),
        bodyLarge = TYPOGRAPHY.bodyLarge.copy(fontFamily = selectedFont),
        bodyMedium = TYPOGRAPHY.bodyMedium.copy(fontFamily = selectedFont),
        bodySmall = TYPOGRAPHY.bodySmall.copy(fontFamily = selectedFont),
        labelLarge = TYPOGRAPHY.labelLarge.copy(fontFamily = selectedFont),
        labelMedium = TYPOGRAPHY.labelMedium.copy(fontFamily = selectedFont),
        labelSmall = TYPOGRAPHY.labelSmall.copy(fontFamily = selectedFont),
    )
}