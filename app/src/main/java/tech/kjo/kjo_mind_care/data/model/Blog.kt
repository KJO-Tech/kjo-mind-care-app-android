package tech.kjo.kjo_mind_care.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.enums.MediaType

data class Blog(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: User = User(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val mediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val likes: Int = 0,
    val reaction: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val categoryId: String? = null,
    val status: BlogStatus = BlogStatus.PENDING,
) {
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(createdAt.seconds, createdAt.nanoseconds, ZoneOffset.UTC)
    }

    @Composable
    fun getTimeAgo(): String {
        val now = LocalDateTime.now()
        val postDateTime = getLocalDateTime()
        val minutes = ChronoUnit.MINUTES.between(postDateTime, now)
        val hours = ChronoUnit.HOURS.between(postDateTime, now)
        val days = ChronoUnit.DAYS.between(postDateTime, now)
        val months = ChronoUnit.MONTHS.between(postDateTime, now)
        val years = ChronoUnit.YEARS.between(postDateTime, now)

        return when {
            years > 0 -> stringResource(
                if (years == 1L) R.string.time_ago_year else R.string.time_ago_years,
                years
            )

            months > 0 -> stringResource(
                if (months == 1L) R.string.time_ago_month else R.string.time_ago_months,
                months
            )

            days > 0 -> stringResource(
                if (days == 1L) R.string.time_ago_day else R.string.time_ago_days,
                days
            )

            hours > 0 -> stringResource(
                if (hours == 1L) R.string.time_ago_hour else R.string.time_ago_hours,
                hours
            )

            minutes > 0 -> stringResource(
                if (minutes == 1L) R.string.time_ago_minute else R.string.time_ago_minutes,
                minutes
            )

            else -> stringResource(R.string.time_ago_now)
        }
    }
}