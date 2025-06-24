package tech.kjo.kjo_mind_care.usecase.blog

import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class DeleteBlogUseCase @Inject constructor(
    private val blogRepository: IBlogRepository
) {
    suspend operator fun invoke(blogId: String): Result<Unit> {
        return blogRepository.updateBlogStatus(blogId, BlogStatus.DELETED)
    }
}