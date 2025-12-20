package tech.kjo.kjo_mind_care.usecase.user

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UpdateUserTokenUseCase @Inject constructor(
    private val notificationRepository: INotificationRepository
) {
    suspend operator fun invoke() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val token = getFcmToken() ?: return
        notificationRepository.sendRegistrationToServer(userId, token)
    }

    private suspend fun getFcmToken(): String? = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("UpdateUserToken", "FCM token fetched: $token")
                continuation.resume(token)
            } else {
                Log.w("UpdateUserToken", "Fetching FCM registration token failed", task.exception)
                continuation.resume(null)
            }
        }
    }
}
