package tech.kjo.kjo_mind_care.data.repository.impl

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): IAuthRepository {

    override suspend fun login(email: String, password: String): Resource<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
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

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Resource<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).await()

            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                return Resource.Error("Firebase user is null after Google sign-in.")
            }

            val userDocRef = firestore.collection("users").document(firebaseUser.uid)
            val userDocument = userDocRef.get().await()

            if (!userDocument.exists()) {
                // El usuario no existe, crearlo
                val newUser = User(
                    uid = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    createdAt = Timestamp.now(),
                    profileImage = firebaseUser.photoUrl?.toString(),
                    role = "user" // Rol por defecto
                )
                userDocRef.set(newUser).await()
            }
            // Si el usuario ya existe, no se hace nada, solo se completa el login.
            Resource.Success(true)

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("credential is null or malformed") == true ->
                    "Credenciales de Google inválidas. Intenta de nuevo."
                e.message?.contains("auth/network-request-failed") == true ->
                    "Error de red al conectar con Firebase."
                e.message?.contains("auth/invalid-credential") == true ->
                    "Credenciales inválidas. (SHA-1 incorrecto o ID de cliente web no coincidente)."
                else ->
                    e.message ?: "Error desconocido al iniciar sesión con Google"
            }
            Resource.Error(errorMessage)
        }
    }

    override suspend fun register(fullName: String, email: String, password: String): Resource<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid?.let {
                uid -> saveUserToFirestore(uid, fullName, email, null)
            } ?: throw Exception("User UID is null after registration")
            Resource.Success("Registro exitoso")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al registrarse")
        }
    }

    private suspend fun saveUserToFirestore(uid: String, fullName: String, email: String, imageUrl: String?) {
        try {
            val user = User(
                uid = uid,
                fullName = fullName,
                email = email,
                createdAt = Timestamp.now(),
                profileImage = imageUrl,
                role = "user"
            )
            firestore.collection("users").document(uid).set(user).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error guardando usuario", e)
            throw e
        }
    }

    override suspend fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun getCurrentUserDetails(): User? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)?.copy(uid = document.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching current user details", e)
            null
        }
    }

    override fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                launch {
                    val userDetails = getCurrentUserDetails()
                    trySend(userDetails)
                }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun getUserDetails(uid: String): User? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.toObject(User::class.java)?.copy(uid = document.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user details for $uid", e)
            null
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
