package com.shub39.grit.core.theme

import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.figtree
import grit.shared.core.generated.resources.google_sans
import grit.shared.core.generated.resources.inter
import grit.shared.core.generated.resources.manrope
import grit.shared.core.generated.resources.montserrat
import grit.shared.core.generated.resources.outfit
import grit.shared.core.generated.resources.poppins_regular
import org.jetbrains.compose.resources.FontResource

enum class Fonts {
    POPPINS,
    INTER,
    MANROPE,
    MONTSERRAT,
    FIGTREE,
    OUTFIT,
    GOOGLE_SANS,
    SYSTEM_DEFAULT

    ;

    companion object {
        fun Fonts.toDisplayString(): String {
            return when (this) {
                POPPINS -> "Poppins"
                INTER -> "Inter"
                MANROPE -> "Manrope"
                MONTSERRAT -> "Montserrat"
                FIGTREE -> "Figtree"
                OUTFIT -> "Outfit"
                GOOGLE_SANS -> "Google Sans"
                SYSTEM_DEFAULT -> "System Default"
            }
        }

        fun Fonts.toFontRes(): FontResource? {
            return when (this) {
                POPPINS -> Res.font.poppins_regular
                INTER -> Res.font.inter
                MANROPE -> Res.font.manrope
                MONTSERRAT -> Res.font.montserrat
                FIGTREE -> Res.font.figtree
                OUTFIT -> Res.font.outfit
                GOOGLE_SANS -> Res.font.google_sans
                SYSTEM_DEFAULT -> null
            }
        }
    }
}