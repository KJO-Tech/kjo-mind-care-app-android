package tech.kjo.kjo_mind_care.data.model

import com.google.firebase.firestore.DocumentId

data class ActivityCategory(
    @DocumentId val id: String = "",
    val localizedName: Map<String, String> = emptyMap(),
    val localizedDescription: Map<String, String> = emptyMap(),
    val iconResName: String = "",
    val order: Int = 0
)

