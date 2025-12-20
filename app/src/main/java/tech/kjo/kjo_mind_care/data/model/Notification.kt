package tech.kjo.kjo_mind_care.data.model

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.NotificationStatus
import tech.kjo.kjo_mind_care.data.enums.NotificationType

data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.UNKNOWN,
    val args: List<String> = emptyList(),
    val timestamp: Timestamp = Timestamp.now(),
    var status: NotificationStatus = NotificationStatus.NEW,
    val targetRoute: String = "",
    val targetId: String? = null,
) {

    @Exclude
    fun getFormattedTitle(context: Context): String {
        return try {
            val titleResId = when (type) {
                NotificationType.LIKE -> R.string.notification_like_blog_title
                NotificationType.COMMENT -> R.string.notification_comment_blog_title
                NotificationType.ACTIVITY_REMINDER -> R.string.notification_activity_reminder_title
                NotificationType.MOOD_REMINDER -> R.string.notification_mood_reminder_title
                NotificationType.NEW_BLOG_POST -> R.string.notification_new_blog_post_title
                NotificationType.SYSTEM -> R.string.notification_system_update_title
                NotificationType.BLOG_APPROVED -> R.string.notification_blog_approved_title
                NotificationType.BLOG_REJECTED -> R.string.notification_blog_rejected_title
                else -> R.string.notification_default_title
            }
            context.getString(titleResId)
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
                    val commenterName = args.getOrNull(0) ?: "Alguien"
                    val commentContent = args.getOrNull(1) ?: "Escribio algo"
                    context.getString(R.string.notification_comment_blog_body, commenterName, commentContent)
                }
                NotificationType.BLOG_APPROVED -> {
                    val blogTitle = args.getOrNull(0) ?: "Tu blog"
                    context.getString(R.string.notification_blog_approved_body, blogTitle)
                }
                NotificationType.BLOG_REJECTED -> {
                    val blogTitle = args.getOrNull(0) ?: "Tu blog"
                    context.getString(R.string.notification_blog_rejected_body, blogTitle)
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
