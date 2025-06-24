package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Comment

interface ICommentRepository {
    fun getCommentsForBlog(blogId: String): Flow<List<Comment>>
    suspend fun addComment(blogId: String, comment: Comment): Result<String>
    suspend fun updateComment(blogId: String, comment: Comment): Result<Unit>
    suspend fun deleteComment(blogId: String, commentId: String): Result<Unit>
}