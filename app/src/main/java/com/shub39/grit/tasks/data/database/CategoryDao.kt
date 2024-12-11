package com.shub39.grit.tasks.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)
}