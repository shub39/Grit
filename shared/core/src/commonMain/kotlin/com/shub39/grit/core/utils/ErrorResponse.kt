package com.shub39.grit.core.utils

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)