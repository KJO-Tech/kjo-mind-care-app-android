package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject

class BlogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: IAuthRepository
) : IBlogRepository {

    private val blogPostsCollection = firestore.collection("blogs")

    override fun getBlogPosts(): Flow<List<Blog>> = flow {
        val userId = authRepository.getCurrentUserUid()
        val snapshot = blogPostsCollection
            .whereEqualTo("status", BlogStatus.PUBLISHED.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()

        val blogPosts = coroutineScope {
            snapshot.documents.map { document ->
                async {
                    val blog = document.toObject(Blog::class.java)?.copy(id = document.id)
                    blog?.apply {
                        val likedBy = document.get("likedBy") as? List<*> ?: emptyList<String>()
                        likes = likedBy.size
                        if (userId != null) {
                            isLiked = likedBy.contains(userId)
                        }
                        val commentsSnapshot = blogPostsCollection.document(id).collection("comments").get().await()
                        comments = commentsSnapshot.size()
                    }
                }
            }.awaitAll().filterNotNull()
        }

        emit(blogPosts.filterIsInstance<Blog>()) // Asegurarse de emitir una lista de Blogs
    }

    override suspend fun getBlogById(blogId: String): Blog? {
        return try {
            val document = blogPostsCollection.document(blogId).get().await()
            document.toObject(Blog::class.java)?.copy(id = document.id)?.apply {
                val userId = authRepository.getCurrentUserUid()
                val likedBy = document.get("likedBy") as? List<*> ?: emptyList<String>()
                likes = likedBy.size
                if (userId != null) {
                    isLiked = likedBy.contains(userId)
                }
                val commentsSnapshot = blogPostsCollection.document(id).collection("comments").get().await()
                comments = commentsSnapshot.size()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createBlog(blogPost: Blog): Result<String> {
        return try {
            val docRef = blogPostsCollection.document()
            val newBlog = blogPost.copy(id = docRef.id, createdAt = Timestamp.now(), status = BlogStatus.PUBLISHED)
            docRef.set(newBlog).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBlog(blogPost: Blog): Result<Unit> {
        return try {
            val updates = mapOf(
                "title" to blogPost.title,
                "content" to blogPost.content,
                "mediaUrl" to blogPost.mediaUrl,
                "mediaType" to blogPost.mediaType,
                "categoryId" to blogPost.categoryId,
                "status" to blogPost.status.name,
                "updatedAt" to Timestamp.now()
            )
            blogPostsCollection.document(blogPost.id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBlogStatus(
        blogId: String,
        status: BlogStatus
    ): Result<Unit> {
        return try {
            blogPostsCollection.document(blogId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserPostsCount(userId: String): Flow<Long> = flow {
        if (userId.isEmpty()) {
            emit(0L)
            return@flow
        }
        val snapshot = blogPostsCollection.whereEqualTo("author.uid", userId).get().await()
        emit(snapshot.size().toLong())
    }
}
