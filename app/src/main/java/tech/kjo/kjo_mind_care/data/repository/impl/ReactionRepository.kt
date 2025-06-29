package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.Reaction
import tech.kjo.kjo_mind_care.data.repository.IReactionRepository

import javax.inject.Inject


class ReactionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): IReactionRepository {

    override suspend fun toggleLike(
        blogId: String,
        userId: String
    ): Result<Boolean> {
        return try {
            val blogRef = firestore.collection("blogs").document(blogId)
            val likeRef = blogRef.collection("reaction").document(userId)

            firestore.runTransaction { transaction ->
                val likeDoc = transaction.get(likeRef)
                val blogDoc = transaction.get(blogRef)

                if (!blogDoc.exists()) {
                    throw Exception("Blog not found: $blogId")
                }

                val currentLikes = blogDoc.getLong("reaction")?.toInt() ?: 0

                if (likeDoc.exists()) {
                    transaction.delete(likeRef)
                    transaction.update(blogRef, "reaction", FieldValue.increment(-1))
                } else {
                    transaction.set(likeRef, mapOf("timestamp" to Timestamp.now()))
                    transaction.update(blogRef, "reaction", FieldValue.increment(1))
                    true
                }
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasUserLikedBlog(
        blogId: String,
        userId: String
    ): Result<Boolean> {
        return try {
            val likeDoc = firestore.collection("blogs").document(blogId)
                .collection("reaction").document(userId)
                .get().await()
            Result.success(likeDoc.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLikesForBlog(blogId: String): Flow<List<Reaction>> {
        return firestore.collection("blogs").document(blogId)
            .collection("reaction")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Reaction::class.java) }
            }
            .catch { e ->
                println("Error getting likes for blog $blogId: $e")
                emit(emptyList())
            }
    }



}