package tech.kjo.kjo_mind_care.usecase.category

import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.repository.ICategoryRepository
import javax.inject.Inject

class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<String> {
        return categoryRepository.createCategory(category)
    }
}