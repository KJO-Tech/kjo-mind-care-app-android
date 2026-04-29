package tech.kjo.kjo_mind_care.usecase.blog

import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class UpdateBlogUseCase @Inject constructor(
    private val blogRepository: IBlogRepository
) {
    suspend operator fun invoke(blogPost: Blog): Result<Unit> {
        return blogRepository.updateBlog(blogPost)
    }
}