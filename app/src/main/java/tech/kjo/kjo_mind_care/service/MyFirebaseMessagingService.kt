package tech.kjo.kjo_mind_care.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import tech.kjo.kjo_mind_care.MainActivity
import tech.kjo.kjo_mind_care.R

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"

    // Se llama cuando se genera un nuevo token
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Firebase.auth.currentUser?.let { user ->
            sendRegistrationToServer(user.uid, token)
        }
    }

    // Se llama cuando se recibe un mensaje mientras la app está en primer plano
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // La Cloud Function envía el título y el cuerpo en el campo 'notification'
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification Body: ${notification.body}")
            // Los datos para el deep link vienen en el campo 'data'
            sendNotification(notification.title, notification.body, remoteMessage.data)
        }
    }

    private fun sendRegistrationToServer(userId: String, token: String?) {
        if (token == null) return

        val db = Firebase.firestore
        val deviceToken = hashMapOf(
            "token" to token,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .collection("deviceTokens")
            .whereEqualTo("token", token)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // El token no existe, lo agregamos
                    db.collection("users").document(userId)
                        .collection("deviceTokens")
                        .add(deviceToken)
                        .addOnSuccessListener { Log.d(TAG, "Token guardado en Firestore exitosamente") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error al guardar el token", e) }
                } else {
                    Log.d(TAG, "El token ya estaba registrado.")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al verificar el token", e)
            }
    }

    fun onUserLogout(userId: String, token: String) {
        val db = Firebase.firestore

        db.collection("users").document(userId)
            .collection("deviceTokens")
            .whereEqualTo("token", token)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                    Log.d(TAG, "Token borrado al cerrar sesión")
                }
            }
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        val targetRoute = data["targetRoute"]
        val targetId = data["targetId"]

        // Crea un Intent para abrir la MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Agrega la ruta y el ID del deep link como extras
            if (targetRoute != null) {
                val deepLinkUri = Uri.parse("kjoapp://app.kjo-mind-care.com/$targetRoute/$targetId")
                action = Intent.ACTION_VIEW
                setData(deepLinkUri)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "kjo_mind_care_notification"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Asegúrate de tener este icono
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Necesario para Android 8.0 (API 26) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
