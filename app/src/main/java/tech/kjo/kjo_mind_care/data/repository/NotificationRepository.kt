package tech.kjo.kjo_mind_care.data.repository

import android.content.Context
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
            // 1. NOTIFICACIÓN DE LIKE EN BLOG
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.LIKE,
                titleKey = R.string.notification_like_blog_title,
                bodyKey = R.string.notification_like_blog_body,
                args = listOf(user1?.fullName ?: "Alguien", blog1?.title ?: "Tu Blog"),
                timestamp = LocalDateTime.now().minusMinutes(5),
                status = NotificationStatus.NEW,
                // ✅ CORRECTO: Usar la ruta interna de navegación
                targetRoute = Screen.BlogPostDetail.createRoute(blog1?.id ?: "sample_blog_1"),
                targetId = blog1?.id
            ),

            // 2. NOTIFICACIÓN DE COMENTARIO
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.COMMENT,
                titleKey = R.string.notification_comment_blog_title,
                bodyKey = R.string.notification_comment_blog_body,
                args = listOf(user1?.fullName ?: "Alguien", blog1?.title ?: "Tu Blog"),
                timestamp = LocalDateTime.now().minusHours(1),
                status = NotificationStatus.NEW,
                // ✅ CORRECTO: Navegar al blog específico
                targetRoute = Screen.BlogPostDetail.createRoute(blog1?.id ?: "sample_blog_2"),
                targetId = blog1?.id
            ),

            // 3. RECORDATORIO DE MOOD
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.MOOD_REMINDER,
                titleKey = R.string.notification_mood_reminder_title,
                bodyKey = R.string.notification_mood_reminder_body,
                timestamp = LocalDateTime.now().minusHours(3),
                status = NotificationStatus.NEW,
                // ✅ CORRECTO: Navegar al mood tracker
                targetRoute = Screen.MoodTrackerStart.route
            ),

            // 4. RECORDATORIO DE ACTIVIDAD
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.ACTIVITY_REMINDER,
                titleKey = R.string.notification_activity_reminder_title,
                bodyKey = R.string.notification_activity_reminder_body,
                args = listOf(activity1?.name ?: "Meditación"),
                timestamp = LocalDateTime.now().minusHours(6),
                status = NotificationStatus.READ,
                // ✅ CORRECTO: Navegar a entrada de mood específica
                targetRoute = Screen.MoodEntryDetail.createRoute(activity1?.id ?: "activity_1"),
                targetId = activity1?.id
            ),

            // 5. NOTIFICACIÓN DEL SISTEMA
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.SYSTEM,
                titleKey = R.string.notification_system_update_title,
                bodyKey = R.string.notification_system_update_body,
                timestamp = LocalDateTime.now().minusDays(1),
                status = NotificationStatus.READ,
                // ✅ CORRECTO: Navegar al perfil
                targetRoute = Screen.ProfileDetails.route
            ),

            // 6. NUEVO POST DE BLOG
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.NEW_BLOG_POST,
                titleKey = R.string.notification_new_blog_post_title,
                bodyKey = R.string.notification_new_blog_post_body,
                args = listOf(user1?.fullName ?: "Un autor", "Nuevas Reflexiones"),
                timestamp = LocalDateTime.now().minusHours(12),
                status = NotificationStatus.READ,
                // ✅ CORRECTO: Navegar al nuevo blog
                targetRoute = Screen.BlogPostDetail.createRoute("new_blog_post_123")
            )
        )
    }
}

// FUNCIÓN PARA CREAR NOTIFICACIONES DE PRUEBA
fun createTestNotification(context: Context, type: String): Notification {
    return when (type) {
        "blog_like" -> Notification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.LIKE,
            titleKey = R.string.notification_like_blog_title,
            bodyKey = R.string.notification_like_blog_body,
            args = listOf("Usuario Test", "Mi Blog de Prueba"),
            timestamp = LocalDateTime.now(),
            status = NotificationStatus.NEW,
            targetRoute = Screen.BlogPostDetail.createRoute("test_blog_123")
        )

        "mood_reminder" -> Notification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.MOOD_REMINDER,
            titleKey = R.string.notification_mood_reminder_title,
            bodyKey = R.string.notification_mood_reminder_body,
            timestamp = LocalDateTime.now(),
            status = NotificationStatus.NEW,
            targetRoute = Screen.MoodTrackerStart.route
        )

        "go_to_profile" -> Notification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.SYSTEM,
            titleKey = R.string.notification_system_update_title,
            bodyKey = R.string.notification_system_update_body,
            timestamp = LocalDateTime.now(),
            status = NotificationStatus.NEW,
            targetRoute = Screen.ProfileDetails.route
        )

        else -> Notification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.SYSTEM,
            titleKey = R.string.app_name,
            bodyKey = R.string.app_name,
            timestamp = LocalDateTime.now(),
            status = NotificationStatus.NEW,
            targetRoute = Screen.HomeStart.route
        )
    }
}