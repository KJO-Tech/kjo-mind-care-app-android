package tech.kjo.kjo_mind_care.usecase.blog

import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class CreateBlogUseCase @Inject constructor(
    private val blogRepository: IBlogRepository
) {
    suspend operator fun invoke(blogPost: Blog): Result<String> {
        return blogRepository.createBlog(blogPost)
    }
}