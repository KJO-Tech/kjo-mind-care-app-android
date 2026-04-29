package tech.kjo.kjo_mind_care.usecase.reaction

import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import tech.kjo.kjo_mind_care.data.repository.IReactionRepository
import tech.kjo.kjo_mind_care.data.repository.impl.ReactionRepository
import javax.inject.Inject

class ToggleBlogLikeUseCase @Inject constructor(
    private val reactionRepository: IReactionRepository
) {
    suspend operator fun invoke(blogId: String, userId: String): Result<Boolean> {
        return reactionRepository.toggleLike(blogId, userId)
    }
}