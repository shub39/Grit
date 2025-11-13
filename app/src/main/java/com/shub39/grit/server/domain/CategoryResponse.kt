package com.shub39.grit.server.domain

import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.CategoryColors
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String,
    val index: Int
)

fun Category.toCategoryResponse(): CategoryResponse {
    return CategoryResponse(
        id = id,
        name = name,
        index = index
    )
}

fun CategoryResponse.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        index = index,
        color = CategoryColors.GRAY.color
    )
}
