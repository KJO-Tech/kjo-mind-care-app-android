package tech.kjo.kjo_mind_care.data.model

import com.google.firebase.Timestamp


data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null,
    var profileImage: String? = null,
    var phone: String? = null,
    var age: Int? = null,
)