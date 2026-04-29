package tech.kjo.kjo_mind_care.usecase.category

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.repository.ICategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}