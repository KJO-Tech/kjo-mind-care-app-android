package tech.kjo.kjo_mind_care.data.repository

import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.utils.Resource

interface IAuthRepository {
    suspend fun login(email: String, password: String): Resource<String>
    suspend fun register(fullName: String, email: String, password: String): Resource<String>
    suspend fun getCurrentUserUid(): String?
    suspend fun getUserDetails(uid: String): User?
    suspend fun logout(): Resource<Unit>
}
