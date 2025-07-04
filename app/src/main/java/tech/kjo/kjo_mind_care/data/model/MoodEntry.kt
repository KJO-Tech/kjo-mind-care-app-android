package tech.kjo.kjo_mind_care.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import tech.kjo.kjo_mind_care.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

data class MoodEntry(
    val id: String = "",
    val mood: MoodType = MoodType.Neutral,
    val createdAt: Timestamp = Timestamp.now(),
    val note: String = "",
    val userId: String = ""
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

enum class MoodType(val iconResId: Int, val nameResId: Int) {
    Happy(R.drawable.ic_mood_happy, R.string.mood_happy),
    Tired(R.drawable.ic_mood_tired, R.string.mood_tired_title),
    Angry(R.drawable.ic_mood_angry, R.string.mood_angry_title),
    Anxious(R.drawable.ic_mood_anxious, R.string.mood_anxious),
    Sad(R.drawable.ic_mood_sad, R.string.mood_sad),
    Neutral(R.drawable.ic_mood_neutral, R.string.mood_neutral),
    Joyful(R.drawable.ic_mood_joyful, R.string.mood_joyful),
    Frustrated(R.drawable.ic_mood_frustrated, R.string.mood_frustrated_title)
}
/*Example
fun sampleEntries() = listOf(
    MoodEntry(MoodType.Happy, LocalDateTime.now().minusDays(1), "Had a great day at work!"),
    MoodEntry(MoodType.Anxious, LocalDateTime.now().minusDays(2), "Worried about upcoming presentation"),
    MoodEntry(MoodType.Sad, LocalDateTime.now().minusDays(3), "Missing friends and family")
)*/