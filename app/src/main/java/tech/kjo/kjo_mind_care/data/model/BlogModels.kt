package tech.kjo.kjo_mind_care.data.model

data class DailyActivity(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val durationMinutes: Int,
    val difficulty: String,
    var isCompleted: Boolean
)