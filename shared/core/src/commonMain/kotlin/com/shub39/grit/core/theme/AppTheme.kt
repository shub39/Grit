package com.shub39.grit.core.theme

import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.dark
import grit.shared.core.generated.resources.light
import grit.shared.core.generated.resources.system
import org.jetbrains.compose.resources.StringResource

enum class AppTheme {
    SYSTEM,
    DARK,
    LIGHT

    ;

    companion object {
        fun AppTheme.toDisplayString(): StringResource {
            return when (this) {
                SYSTEM -> Res.string.system
                DARK -> Res.string.dark
                LIGHT -> Res.string.light
            }
        }
    }
}