/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.google_sans_flex
import grit.shared.core.generated.resources.poppins_regular
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

val TYPOGRAPHY = Typography()

@Composable
fun flexFontEmphasis(slant: Float = 0f): FontFamily =
    FontFamily(
        Font(
            resource = Res.font.google_sans_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(1000),
                    FontVariation.slant(slant),
                    FontVariation.width(120f),
                ),
        )
    )

@Composable
fun flexFontBold(slant: Float = 0f): FontFamily =
    FontFamily(
        Font(
            resource = Res.font.google_sans_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(800),
                    FontVariation.slant(slant),
                    FontVariation.width(100f),
                ),
        )
    )

@Composable
fun flexFontRounded(): FontFamily =
    FontFamily(
        Font(
            resource = Res.font.google_sans_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(700),
                    FontVariation.Setting("ROND", 100f),
                ),
        )
    )

/** function to get custom typography for Grit */
@Composable
fun provideTypography(font: FontResource? = Res.font.poppins_regular): Typography {
    val selectedFont = font?.let { FontFamily(Font(font)) } ?: FontFamily.Default

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

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun TypographyPreview() {
    val typography = provideTypography()
    Column {
        Text("Flex Font Emphasis", fontFamily = flexFontEmphasis())
        Text("Flex Font Bold", fontFamily = flexFontBold())
        Text("Flex Font Rounded", fontFamily = flexFontRounded())

        Text("Display Large", style = typography.displayLarge)
        Text("Display Medium", style = typography.displayMedium)
        Text("Display Small", style = typography.displaySmall)
        Text("Headline Large", style = typography.headlineLarge)
        Text("Headline Medium", style = typography.headlineMedium)
        Text("Headline Small", style = typography.headlineSmall)
        Text("Title Large", style = typography.titleLarge)
        Text("Title Medium", style = typography.titleMedium)
        Text("Title Small", style = typography.titleSmall)
        Text("Body Large", style = typography.bodyLarge)
        Text("Body Medium", style = typography.bodyMedium)
        Text("Body Small", style = typography.bodySmall)
        Text("Label Large", style = typography.labelLarge)
        Text("Label Medium", style = typography.labelMedium)
        Text("Label Small", style = typography.labelSmall)
    }
}
