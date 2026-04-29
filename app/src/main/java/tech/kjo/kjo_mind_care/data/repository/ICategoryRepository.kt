package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Category

interface ICategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getCategoryById(categoryId: String): Category?
    suspend fun createCategory(category: Category): Result<String>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
}