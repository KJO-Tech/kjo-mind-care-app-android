package tech.kjo.kjo_mind_care.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

class Reaction {
    val userId: String = ""
    val timestamp: Timestamp = Timestamp.now()
}