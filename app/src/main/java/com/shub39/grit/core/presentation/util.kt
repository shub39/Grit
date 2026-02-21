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
package com.shub39.grit.core.presentation

import kotlin.random.Random

fun getRandomLine(): String {
    return when (Random.nextInt(0, 10)) {
        1 -> "Bombardino Crocodilo"
        2 -> "Brr Brr Patapim"
        3 -> "Lirili Larila"
        4 -> "Trippi Troppi"
        5 -> "Capucino Assassaino"
        6 -> "Trulimero Trulichina"
        7 -> "Tung Tung Tung Sahur"
        8 -> "Chimpanzini Bananini"
        9 -> "Giraffa Celeste"
        else -> "Tralalero Tralala"
    }
}
