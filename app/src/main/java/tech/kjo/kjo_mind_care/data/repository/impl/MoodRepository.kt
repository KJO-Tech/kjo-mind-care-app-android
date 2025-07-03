package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): IMoodRepository {

    private val moodCollection = firestore.collection("moodEntries")

    override suspend fun getMoodEntries(userId: String): Flow<List<MoodEntry>> = callbackFlow {
        val subscription = moodCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val moodEntries = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(MoodEntry::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(moodEntries).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveMoodEntry(moodEntry: MoodEntry): Result<String> {
        return try {
            val docRef = moodCollection.add(moodEntry.copy( createdAt = Timestamp.now())).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}