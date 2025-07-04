package tech.kjo.kjo_mind_care.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.utils.Resource

interface IAuthRepository {
    suspend fun login(email: String, password: String): Resource<String>
    suspend fun signInWithGoogle(accountToken: GoogleSignInAccount): Resource<Boolean>
    suspend fun register(fullName: String, email: String, password: String): Resource<String>
    suspend fun getCurrentUserUid(): String?
    suspend fun getCurrentUserDetails(): User?
    fun observeCurrentUser(): Flow<User?>
    suspend fun getUserDetails(uid: String): User?
    suspend fun logout(): Result<Unit>
}
