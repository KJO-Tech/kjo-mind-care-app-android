package tech.kjo.kjo_mind_care.usecase.comments

import tech.kjo.kjo_mind_care.data.model.Comment
import tech.kjo.kjo_mind_care.data.repository.ICommentRepository
import javax.inject.Inject

class UpdateCommentUseCase @Inject constructor(
    private val commentRepository: ICommentRepository
) {
    suspend operator fun invoke(blogId: String, comment: Comment): Result<Unit> {
        return commentRepository.updateComment(blogId, comment)
    }
}