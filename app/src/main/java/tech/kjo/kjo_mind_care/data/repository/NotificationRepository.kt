package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.model.NotificationStatus
import tech.kjo.kjo_mind_care.data.model.NotificationType
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import java.time.LocalDateTime
import java.util.UUID

class NotificationRepository {

    private val _notifications = MutableStateFlow(generateSampleNotifications().toMutableList())
    val notifications = _notifications.asStateFlow()

    fun markNotificationAsRead(notificationId: String) {
        _notifications.update { currentList ->
            currentList.map {
                if (it.id == notificationId) it.copy(status = NotificationStatus.READ) else it
            }.toMutableList()
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { currentList ->
            currentList.map { it.copy(status = NotificationStatus.READ) }.toMutableList()
        }
    }

    fun addNotification(notification: Notification) {
        _notifications.update { currentList ->
            val updatedList = currentList.toMutableList()
            updatedList.add(0, notification) // Añadir al principio
            updatedList
        }
    }

    // Función para generar notificaciones de ejemplo (puedes personalizarla)
    private fun generateSampleNotifications(): List<Notification> {
        val blog1 = StaticBlogData.getSampleBlogPosts().firstOrNull()
        val user1 = StaticBlogData.getSampleUsers().firstOrNull { it.uid != StaticBlogData.currentUser.uid }
        val activity1 = StaticBlogData.getSampleDailyActivities().firstOrNull()

        return listOf(
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.LIKE,
                titleKey = R.string.notification_like_blog_title,
                bodyKey = R.string.notification_like_blog_body,
                args = listOf(user1?.fullName ?: "Alguien", blog1?.title ?: "Tu Blog"),
                timestamp = LocalDateTime.now().minusMinutes(5),
                status = NotificationStatus.NEW,
                targetRoute = blog1?.let { Screen.BlogPostDetail.createRoute(it.id) } ?: ""
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.COMMENT,
                titleKey = R.string.notification_comment_blog_title,
                bodyKey = R.string.notification_comment_blog_body,
                args = listOf(user1?.fullName ?: "Alguien", blog1?.title ?: "Tu Blog"),
                timestamp = LocalDateTime.now().minusHours(1),
                status = NotificationStatus.NEW,
                targetRoute = blog1?.let { Screen.BlogPostDetail.createRoute(it.id) } ?: ""
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.MOOD_REMINDER,
                titleKey = R.string.notification_mood_reminder_title,
                bodyKey = R.string.notification_mood_reminder_body,
                timestamp = LocalDateTime.now().minusDays(1),
                status = NotificationStatus.READ,
                targetRoute = Screen.MoodTrackerStart.route // Asume esta ruta para el mood tracker
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.ACTIVITY_REMINDER,
                titleKey = R.string.notification_activity_reminder_title,
                bodyKey = R.string.notification_activity_reminder_body,
                args = listOf(activity1?.name ?: "Meditación"),
                timestamp = LocalDateTime.now().minusDays(2),
                status = NotificationStatus.READ,
                targetRoute = activity1?.let { Screen.MoodEntryDetail.createRoute(it.id) } ?: "" // O a la lista de actividades
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.SYSTEM,
                titleKey = R.string.notification_system_update_title,
                bodyKey = R.string.notification_system_update_body,
                timestamp = LocalDateTime.now().minusDays(5),
                status = NotificationStatus.READ,
                targetRoute = Screen.ProfileDetails.route // Ejemplo: llevar a perfil para ver cambios o a una pantalla de "Novedades"
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.NEW_BLOG_POST,
                titleKey = R.string.notification_new_blog_post_title,
                bodyKey = R.string.notification_new_blog_post_body,
                args = listOf(user1?.fullName ?: "Un autor", "Nuevas Reflexiones"),
                timestamp = LocalDateTime.now().minusDays(3),
                status = NotificationStatus.READ,
                targetRoute = blog1?.let { Screen.BlogPostDetail.createRoute(it.id) } ?: ""
            )
        )
    }
}