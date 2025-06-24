package tech.kjo.kjo_mind_care.usecase.blog

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class GetBlogUseCase @Inject constructor(
    private val blogRepository: IBlogRepository
) {
    operator fun invoke(): Flow<List<Blog>> {
        return blogRepository.getBlogPosts()
    }
}