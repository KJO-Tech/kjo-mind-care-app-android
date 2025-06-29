package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Blog

interface IBlogRepository {
    fun getBlogPosts(): Flow<List<Blog>>
    suspend fun getBlogById(blogId: String): Blog?
    suspend fun createBlog(blogPost: Blog): Result<String>
    suspend fun updateBlog(blogPost: Blog): Result<Unit>
    suspend fun updateBlogStatus(blogId: String, status: BlogStatus): Result<Unit>

}