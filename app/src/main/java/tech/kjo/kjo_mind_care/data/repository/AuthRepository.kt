package tech.kjo.kjo_mind_care.data.repository

import com.google.firebase.auth.FirebaseUser

import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.utils.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<String>
    suspend fun register(fullName: String, email: String, password: String): Resource<String>
}
