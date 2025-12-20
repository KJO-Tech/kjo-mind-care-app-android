package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.Mood
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IMoodRepository {

    private val moodEntryCollection = firestore.collection("moodEntries")
    private val moodCollection = firestore.collection("moods")

    override suspend fun getMoodEntries(userId: String): Flow<List<MoodEntry>> = callbackFlow {
        val subscription = moodEntryCollection
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
            val docRef =
                moodEntryCollection.add(moodEntry.copy(createdAt = Timestamp.now())).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMoodEntriesCountByUserId(userId: String): Flow<Long> = callbackFlow {
        val query = moodEntryCollection.whereEqualTo("userId", userId)
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val count = snapshot?.size()?.toLong() ?: 0L
            trySend(count).isSuccess
        }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun getMoods(): Flow<List<Mood>> = callbackFlow {
        val subscription = moodCollection
            .orderBy("value", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val moods = snapshot.documents.mapNotNull { document ->
                        document.toObject(Mood::class.java)?.copy(id = document.id)
                    }
                    trySend(moods).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }
}