package tech.kjo.kjo_mind_care.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import tech.kjo.kjo_mind_care.R

data class Comment(
    val id: String = "",
    val author: User = User(),
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val isMine: Boolean = false,
    val parentCommentId: String? = null,
    val replies: List<Comment> = emptyList()
) {
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(createdAt.seconds, createdAt.nanoseconds, ZoneOffset.UTC)
    }

    @Composable
    fun getTimeAgo(): String {
        val now = LocalDateTime.now()
        val commentDateTime = getLocalDateTime()
        val minutes = ChronoUnit.MINUTES.between(commentDateTime, now)
        val hours = ChronoUnit.HOURS.between(commentDateTime, now)
        val days = ChronoUnit.DAYS.between(commentDateTime, now)
        val months = ChronoUnit.MONTHS.between(commentDateTime, now)
        val years = ChronoUnit.YEARS.between(commentDateTime, now)

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