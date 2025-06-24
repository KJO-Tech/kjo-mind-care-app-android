package tech.kjo.kjo_mind_care.usecase.comments

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Comment
import tech.kjo.kjo_mind_care.data.repository.ICommentRepository
import javax.inject.Inject

class GetCommentsForBlogUseCase @Inject constructor(
    private val commentRepository: ICommentRepository
) {
    operator fun invoke(blogId: String): Flow<List<Comment>> {
        return commentRepository.getCommentsForBlog(blogId)
    }
}