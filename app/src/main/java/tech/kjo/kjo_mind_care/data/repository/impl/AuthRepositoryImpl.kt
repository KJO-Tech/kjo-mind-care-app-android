package tech.kjo.kjo_mind_care.data.repository.impl

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.AuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): AuthRepository {


    override suspend fun login(email: String, password: String): Resource<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success("Inicio de sesión exitoso")
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                    "Correo o contraseña incorrectos"
                e.message?.contains("There is no user record") == true ->
                    "El usuario no existe"
                else ->
                    "Error al iniciar sesión: ${e.message}"
            }
            Resource.Error(errorMessage)
        }
    }

    override suspend fun register(fullName: String, email: String, password: String): Resource<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            saveUserToFirestore(result.user!!.uid, fullName, email)
            Resource.Success("Registro exitoso")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al registrarse")
        }
    }

    private suspend fun saveUserToFirestore(uid: String, fullName: String, email: String) {
        try {
            val user = User(
                uid = uid,
                fullName = fullName,
                email = email,
                createdAt = Timestamp.now()
            )
            firestore.collection("users").document(uid).set(user).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error guardando usuario", e)
            throw e
        }
    }
}
