package com.shub39.grit.core.domain

import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.dark
import grit.shared.core.generated.resources.light
import grit.shared.core.generated.resources.system
import org.jetbrains.compose.resources.StringResource

enum class AppTheme(
    val fullName: StringResource
) {
    SYSTEM(Res.string.system),
    DARK(Res.string.dark),
    LIGHT(Res.string.light)
}