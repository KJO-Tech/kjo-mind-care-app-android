package tech.kjo.kjo_mind_care.usecase.blog

import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class GetBlogByIdUseCase @Inject constructor(
    private val blogRepository: IBlogRepository
) {
    suspend operator fun invoke(blogId: String): Blog? {
        return blogRepository.getBlogById(blogId)
    }
}