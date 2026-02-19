package com.shub39.grit.core.presentation

import kotlin.random.Random

fun getRandomLine(): String {
    return when(Random.nextInt(0, 10)) {
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