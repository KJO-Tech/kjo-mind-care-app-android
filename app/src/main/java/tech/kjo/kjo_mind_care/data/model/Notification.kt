package tech.kjo.kjo_mind_care.data.model

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.NotificationStatus
import tech.kjo.kjo_mind_care.data.enums.NotificationType

data class Notification(
    @get:DocumentId val id: String = "",
    val type: NotificationType = NotificationType.UNKNOWN,
    val args: List<String> = emptyList(),
    val timestamp: Timestamp = Timestamp.now(),
    var status: NotificationStatus = NotificationStatus.NEW,
    val targetRoute: String = "",
    val targetId: String? = null,
) {

    /**
     * Genera el título formateado basado en el tipo de notificación.
     * Corregido para manejar strings con y sin argumentos de formato.
     */
    @Exclude
    fun getFormattedTitle(context: Context): String {
        return try {
            when (type) {
                NotificationType.LIKE -> {
                    context.getString(R.string.notification_like_blog_title)
                }
                NotificationType.COMMENT -> {
                    context.getString(R.string.notification_comment_blog_title)
                }
                NotificationType.ACTIVITY_REMINDER -> {
                    context.getString(R.string.notification_activity_reminder_title)
                }
                NotificationType.MOOD_REMINDER -> context.getString(R.string.notification_mood_reminder_title)
                NotificationType.NEW_BLOG_POST -> context.getString(R.string.notification_new_blog_post_title)
                NotificationType.SYSTEM -> context.getString(R.string.notification_system_update_title)
                else -> context.getString(R.string.notification_default_title)
            }
        } catch (e: Exception) {
            context.getString(R.string.notification_default_title)
        }
    }

    @Exclude
    fun getFormattedBody(context: Context): String {
        return try {
            when (type) {
                NotificationType.LIKE -> {
                    val likerName = args.getOrNull(0) ?: "Alguien"
                    val blogTitle = args.getOrNull(1) ?: "tu publicación"
                    context.getString(R.string.notification_like_blog_body, likerName, blogTitle)
                }
                NotificationType.COMMENT -> {
                    args.getOrNull(1) ?: ""
                }
                NotificationType.MOOD_REMINDER -> context.getString(R.string.notification_mood_reminder_body)
                NotificationType.ACTIVITY_REMINDER -> context.getString(R.string.notification_activity_reminder_body)
                NotificationType.NEW_BLOG_POST -> context.getString(R.string.notification_new_blog_post_body)
                NotificationType.SYSTEM -> context.getString(R.string.notification_system_update_body)
                else -> context.getString(R.string.notification_default_body)
            }
        } catch (e: Exception) {
            args.joinToString(" ")
        }
    }
}
