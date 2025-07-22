package com.example.habittrackerapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.habittrackerapp.MainActivity
import com.example.habittrackerapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingUtil : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("MessagingUtil", "Message received: ${remoteMessage.data}")

        // Get the user that sent the message
        val senderId = remoteMessage.data["senderId"]

        // If the sender is the current user, ignore the notification
        if (senderId == FirebaseUtil.getCurrentUserId()) {
            return
        }

        // Get the click_action and other data from the message
        val clickAction = remoteMessage.data["click_action"]

        // If the app is in the background or closed, show a notification as usual
        if (clickAction != null) {
            // Create a notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 0 // Unique ID for notification

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("click_action", clickAction)
            }
            val pendingIntent = PendingIntent.getActivity(
                this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, "@string/notification_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "@string/notification_channel_id"
        createNotificationChannel(channelId) // Create the channel if needed

        // Intent to open MainActivity when the notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = Random.nextInt() // Unique ID for each notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle(title ?: "New Message")
            .setContentText(message ?: "You have a new notification")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Remove notification when tapped
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel(channelId: String) {
        val channel = NotificationChannel(
            channelId, "Messages", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for FCM messages"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseUtil.saveTokenToFirestore(token) // Save or update the token in Firestore
    }
}
