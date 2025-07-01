package tech.kjo.kjo_mind_care.data.model

import com.google.firebase.firestore.DocumentId

enum class ExerciseContentType {
    VIDEO,
    AUDIO,
    TEXT,
    GAME
}

data class DailyExercise(
    @DocumentId val id: String = "",
    val categoryId: String = "",
    val localizedTitle: Map<String, String> = emptyMap(),
    val localizedDescription: Map<String, String> = emptyMap(),
    val durationMinutes: Int = 0,
    val contentType: ExerciseContentType = ExerciseContentType.TEXT,
    val contentUrl: String = "",
    val localizedContentText: Map<String, String> = emptyMap(),
    val thumbnailUrl: String = "",
    val difficulty: String = "Beginner",
    val tags: List<String> = emptyList()
)