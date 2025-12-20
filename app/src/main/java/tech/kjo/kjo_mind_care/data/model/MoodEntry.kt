package tech.kjo.kjo_mind_care.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import tech.kjo.kjo_mind_care.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class MoodEntry(
    val id: String = "",
    val moodId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val note: String = "",
    val userId: String = ""
) {
    private fun getZonedDateTime(): ZonedDateTime {
        val instant = Instant.ofEpochSecond(createdAt.seconds, createdAt.nanoseconds.toLong())
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    @Composable
    fun getTimeAgo(): String {
        val now = ZonedDateTime.now()
        val postDateTime = getZonedDateTime()
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
