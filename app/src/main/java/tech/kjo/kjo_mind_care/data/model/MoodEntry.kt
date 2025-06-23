package tech.kjo.kjo_mind_care.data.model

import tech.kjo.kjo_mind_care.R
import java.time.LocalDateTime

data class MoodEntry(
    val mood: MoodType,
    val timestamp: LocalDateTime,
    val note: String
)

enum class MoodType(val iconResId: Int, val nameResId: Int) {
    Happy(R.drawable.ic_mood_happy, R.string.mood_happy),
    Anxious(R.drawable.ic_mood_anxious, R.string.mood_anxious),
    Sad(R.drawable.ic_mood_sad, R.string.mood_sad),
    Neutral(R.drawable.ic_mood_neutral, R.string.mood_neutral),
    Joyful(R.drawable.ic_mood_joyful, R.string.mood_joyful)
}
//Example
fun sampleEntries() = listOf(
    MoodEntry(MoodType.Happy, LocalDateTime.now().minusDays(1), "Had a great day at work!"),
    MoodEntry(MoodType.Anxious, LocalDateTime.now().minusDays(2), "Worried about upcoming presentation"),
    MoodEntry(MoodType.Sad, LocalDateTime.now().minusDays(3), "Missing friends and family")
)