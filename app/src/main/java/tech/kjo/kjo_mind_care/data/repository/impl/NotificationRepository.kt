package tech.kjo.kjo_mind_care.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : INotificationRepository {

    private val TAG = "NotificationRepo"

    private fun userNotifications(userId: String) = firestore.collection("users").document(userId).collection("notifications")

    override fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = userNotifications(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull {
                    it.toObject(Notification::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addNotification(userId: String, notification: Notification): Result<String> {
        return try {
            Log.d(TAG, "Attempting to add notification for user: $userId")
            val docRef = userNotifications(userId).add(notification).await()
            Log.i(TAG, "Notification successfully added with ID: ${docRef.id} for user: $userId")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding notification for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun markNotificationAsRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            userNotifications(userId).document(notificationId)
                .update("status", "READ").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val querySnapshot = userNotifications(userId).whereEqualTo("status", "NEW").get().await()
            for (document in querySnapshot.documents) {
                batch.update(document.reference, "status", "READ")
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking all notifications as read for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun sendRegistrationToServer(userId: String, token: String): Result<Unit> {
        return try {
            val tokenData = mapOf("token" to token)
            firestore.collection("users").document(userId)
                .collection("deviceTokens").document(token)
                .set(tokenData).await()
            Log.d(TAG, "FCM token successfully sent to server for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending FCM token to server for user: $userId", e)
            Result.failure(e)
        }
    }
}