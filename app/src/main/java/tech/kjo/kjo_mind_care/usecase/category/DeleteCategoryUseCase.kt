package tech.kjo.kjo_mind_care.usecase.category

import android.R.attr.category
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.repository.ICategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Result<Unit> {
        return categoryRepository.deleteCategory(categoryId)
    }
}