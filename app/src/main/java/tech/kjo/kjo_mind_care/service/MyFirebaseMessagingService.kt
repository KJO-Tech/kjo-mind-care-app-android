package tech.kjo.kjo_mind_care.service

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import tech.kjo.kjo_mind_care.data.enums.NotificationType
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.utils.NotificationUtils

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Firebase.auth.currentUser?.let { user ->
            sendRegistrationToServer(user.uid, token)
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "FROM: ${remoteMessage.from}")
        Log.d(TAG, "Message data payload: " + remoteMessage.data)

        val data = remoteMessage.data

        val notificationType = try {
            NotificationType.valueOf(data["type"] ?: "UNKNOWN")
        } catch (e: IllegalArgumentException) {
            NotificationType.UNKNOWN
        }

        val args = mutableListOf<String>()
        data["arg0"]?.let { args.add(it) }
        data["arg1"]?.let { args.add(it) }

        val tempNotification = Notification(
            id = data["notificationId"] ?: System.currentTimeMillis().toString(),
            type = notificationType,
            args = args,
            targetRoute = data["targetRoute"] ?: ""
        )

        NotificationUtils.showSystemNotification(this, tempNotification)
    }

    fun sendRegistrationToServer(userId: String, token: String?) {
        if (token == null) return
        val deviceToken = hashMapOf("token" to token)
        Firebase.firestore.collection("users").document(userId)
            .collection("deviceTokens").document(token)
            .set(deviceToken)
            .addOnSuccessListener { Log.d(TAG, "Token actualizado en Firestore exitosamente.") }
            .addOnFailureListener { e -> Log.w(TAG, "Error al guardar el token", e) }
    }

    fun onUserLogout(userId: String, token: String?) {
        if (token == null) return
        Firebase.firestore.collection("users").document(userId)
            .collection("deviceTokens").document(token)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "Token eliminado de Firestore al cerrar sesión.") }
            .addOnFailureListener { e -> Log.w(TAG, "Error al eliminar el token al cerrar sesión.", e) }
    }

}
