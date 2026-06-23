package com.shub39.grit.shared.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ColorScheme.animated(): ColorScheme {
    val spec: AnimationSpec<Color> = tween(400)
    return ColorScheme(
        primary = animateColorAsState(primary, spec, label = "primary").value,
        onPrimary = animateColorAsState(onPrimary, spec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(primaryContainer, spec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer, spec, label = "onPrimaryContainer").value,
        inversePrimary = animateColorAsState(inversePrimary, spec, label = "inversePrimary").value,
        secondary = animateColorAsState(secondary, spec, label = "secondary").value,
        onSecondary = animateColorAsState(onSecondary, spec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(secondaryContainer, spec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer, spec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(tertiary, spec, label = "tertiary").value,
        onTertiary = animateColorAsState(onTertiary, spec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(tertiaryContainer, spec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(onTertiaryContainer, spec, label = "onTertiaryContainer").value,
        background = animateColorAsState(background, spec, label = "background").value,
        onBackground = animateColorAsState(onBackground, spec, label = "onBackground").value,
        surface = animateColorAsState(surface, spec, label = "surface").value,
        onSurface = animateColorAsState(onSurface, spec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(surfaceVariant, spec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant, spec, label = "onSurfaceVariant").value,
        surfaceTint = animateColorAsState(surfaceTint, spec, label = "surfaceTint").value,
        inverseSurface = animateColorAsState(inverseSurface, spec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(inverseOnSurface, spec, label = "inverseOnSurface").value,
        error = animateColorAsState(error, spec, label = "error").value,
        onError = animateColorAsState(onError, spec, label = "onError").value,
        errorContainer = animateColorAsState(errorContainer, spec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(onErrorContainer, spec, label = "onErrorContainer").value,
        outline = animateColorAsState(outline, spec, label = "outline").value,
        outlineVariant = animateColorAsState(outlineVariant, spec, label = "outlineVariant").value,
        scrim = animateColorAsState(scrim, spec, label = "scrim").value,
        surfaceBright = animateColorAsState(surfaceBright, spec, label = "surfaceBright").value,
        surfaceContainer = animateColorAsState(surfaceContainer, spec, label = "surfaceContainer").value,
        surfaceContainerHigh = animateColorAsState(surfaceContainerHigh, spec, label = "surfaceContainerHigh").value,
        surfaceContainerHighest = animateColorAsState(surfaceContainerHighest, spec, label = "surfaceContainerHighest").value,
        surfaceContainerLow = animateColorAsState(surfaceContainerLow, spec, label = "surfaceContainerLow").value,
        surfaceContainerLowest = animateColorAsState(surfaceContainerLowest, spec, label = "surfaceContainerLowest").value,
        surfaceDim = animateColorAsState(surfaceDim, spec, label = "surfaceDim").value,
    )
}
