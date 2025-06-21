package com.moviles.yetify.network

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moviles.yetify.R

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token actualizado: $token")
        // TOKEN
        saveTokenToPreferences(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Standard notification (when the app is in the foreground)// Custom data handling (for background/foreground notifications)
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "Yetify - Tareas",
                message = notification.body ?: "¡Revisa tus tareas!"
            )
        }

        // Custom data handling (for background/foreground notifications)
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            if (type == "task_reminder") {
                val pending = remoteMessage.data["pending"] ?: "0"
                val inProgress = remoteMessage.data["in_progress"] ?: "0"

                val message = buildTaskMessage(pending.toInt(), inProgress.toInt())
                showNotification(
                    title = "Recordatorio de Tareas",
                    message = message
                )
            }
        }
    }

    private fun buildTaskMessage(pending: Int, inProgress: Int): String {
        return when {
            pending > 0 && inProgress > 0 -> "Tienes $pending tareas pendientes y $inProgress en progreso"
            pending > 0 -> "Tienes $pending tareas pendientes"
            inProgress > 0 -> "Tienes $inProgress tareas en progreso"
            else -> "No tienes tareas pendientes"
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "yetify_channel")
            .setSmallIcon(R.drawable.ic_notification) // Uses a specific icon for notifications
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1001, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "yetify_channel",
                "Recordatorios Yetify",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de tareas pendientes y en progreso"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun saveTokenToPreferences(token: String) {
        val prefs = getSharedPreferences("YetifyPrefs", MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }
}