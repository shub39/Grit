package com.shub39.grit.core.tasks.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val index: Int = 0,
    val color: String
)