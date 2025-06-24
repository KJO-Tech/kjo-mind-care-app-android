package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.Comment
import tech.kjo.kjo_mind_care.data.repository.ICommentRepository
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): ICommentRepository {

    private fun getCommentsCollection(blogId: String) = firestore.collection("blogs").document(blogId).collection("comments")

    override fun getCommentsForBlog(blogId: String): Flow<List<Comment>> = callbackFlow {
        val subscription = getCommentsCollection(blogId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(Comment::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            println("Error deserializing comment: ${e.message}")
                            null
                        }
                    }
                    trySend(comments).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addComment(
        blogId: String,
        comment: Comment
    ): Result<String> {
        return try {
            val docRef = getCommentsCollection(blogId)
                .add(comment.copy(createdAt = Timestamp.now()))
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateComment(
        blogId: String,
        comment: Comment
    ): Result<Unit> {
        return try {
            getCommentsCollection(blogId).document(comment.id)
                .update(mapOf(
                    "content" to comment.content,
                    "createdAt" to Timestamp.now()
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(
        blogId: String,
        commentId: String
    ): Result<Unit> {
        return try {
            getCommentsCollection(blogId).document(commentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}