package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.repository.IBlogRepository
import javax.inject.Inject
import kotlin.jvm.java

class BlogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : IBlogRepository {

    private val blogPostsCollection = firestore.collection("blogs")

    override fun getBlogPosts(): Flow<List<Blog>> = callbackFlow {
        val subscription = blogPostsCollection
            .whereEqualTo("status", BlogStatus.PUBLISHED.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val blogPosts = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(Blog::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(blogPosts).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getBlogById(blogId: String): Blog? {
        return try {
            val document = blogPostsCollection.document(blogId).get().await()
            document.toObject(Blog::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createBlog(blogPost: Blog): Result<String> {
        return try {
            val docRef = blogPostsCollection.add(blogPost.copy(createdAt = Timestamp.now(), status = BlogStatus.PUBLISHED)).await()
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
                "likes" to blogPost.likes,
                "comments" to blogPost.comments,
                "isLiked" to blogPost.isLiked,
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


}