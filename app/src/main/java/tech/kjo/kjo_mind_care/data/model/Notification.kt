package tech.kjo.kjo_mind_care.data.model

import java.time.LocalDateTime

enum class NotificationType {
    LIKE,
    COMMENT,
    MOOD_REMINDER,
    ACTIVITY_REMINDER,
    SYSTEM,
    NEW_BLOG_POST
}

enum class NotificationStatus {
    NEW,
    READ
}

data class Notification(
    val id: String,
    val type: NotificationType,
    val titleKey: Int, // String resource ID for title
    val bodyKey: Int,  // String resource ID for body
    val args: List<String> = emptyList(), // Arguments for string formatting (e.g., username, blog title)
    val timestamp: LocalDateTime,
    var status: NotificationStatus,
    val targetRoute: String, // Deeplink route to navigate to (e.g., "blog_post_detail/blogId123")
    val targetId: String? = null // ID of the target entity (blogId, activityId, etc.)
) {
    fun getFormattedTitle(): String {
        return StaticBlogData.applicationContext?.getString(titleKey) ?: "Notification"
    }

    fun getFormattedBody(): String {
        return if (args.isNotEmpty()) {
            StaticBlogData.applicationContext?.getString(bodyKey, *args.toTypedArray()) ?: "Notification Body"
        } else {
            StaticBlogData.applicationContext?.getString(bodyKey) ?: "Notification Body"
        }
    }
}