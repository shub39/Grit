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
    SYSTEM_DEFAULT;

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
