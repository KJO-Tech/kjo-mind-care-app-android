package tech.kjo.kjo_mind_care.data.repository

import tech.kjo.kjo_mind_care.data.model.Reaction

interface IReactionRepository {
    suspend fun toggleLike(blogId: String, userId: String): Result<Boolean>
    suspend fun hasUserLikedBlog(blogId: String, userId: String): Result<Boolean>
    fun getLikesForBlog(blogId: String): kotlinx.coroutines.flow.Flow<List<Reaction>>
}