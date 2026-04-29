package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Notification

interface INotificationRepository {
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun addNotification(userId: String, notification: Notification): Result<String>
    suspend fun markNotificationAsRead(userId: String, notificationId: String): Result<Unit>
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit>
    suspend fun sendRegistrationToServer(userId: String, token: String): Result<Unit>
}
