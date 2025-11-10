package com.shub39.grit.server.domain

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)