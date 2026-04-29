package tech.kjo.kjo_mind_care.usecase.comments

import tech.kjo.kjo_mind_care.data.repository.ICommentRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentRepository: ICommentRepository
) {
    suspend operator fun invoke(blogId: String, commentId: String): Result<Unit> {
        return commentRepository.deleteComment(blogId, commentId)
    }
}