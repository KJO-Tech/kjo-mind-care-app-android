package tech.kjo.kjo_mind_care.usecase.blog

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class GetUserPostsCountUseCase @Inject constructor(
    private val postRepository: IBlogRepository
) {
    operator fun invoke(userId: String): Flow<Long> {
        return postRepository.getUserPostsCount(userId)
    }
}