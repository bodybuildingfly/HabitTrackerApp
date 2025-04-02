package com.example.habittrackerapp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

object PermissionUtil {

    @Composable
    fun NotificationPermissionRequest(
        context: Context
    ) {
        // Create a notification channel
        createNotificationChannel(context)

        // Check if the notification permission is granted
        var hasPermission by remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            } else {
                mutableStateOf(true)
            }
        }

        // Request the notification permission
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasPermission = isGranted
            if (isGranted) {
                FirebaseUtil.listenForUpdates(
                    listOf("notes/rules", "notes/limits", "notes/ideas", "notes/notes")
                ) { path, _ ->
                    val updatesPath = path.removePrefix("notes/").replaceFirstChar { it.uppercase() }
                    NotificationUtil.showNotification(context, "$updatesPath Updated", "$updatesPath has been updated")
                }
            } else {
                // Handle the case when the user denies the permission
            }
        }

        // Launch the permission request when needed
        LaunchedEffect(key1 = hasPermission) {
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (hasPermission) {
                FirebaseUtil.listenForUpdates(
                    listOf("notes/rules", "notes/limits", "notes/ideas", "notes/notes")
                ) { path, _ ->
                    val updatesPath = path.removePrefix("notes/").replaceFirstChar { it.uppercase() }
                    NotificationUtil.showNotification(context, "$updatesPath Updated", "$updatesPath has been updated")
                }
            }
        }
    }

    fun createNotificationChannel(context: Context) {
        val channelId = "firebase_changes"
        val channelName = "Data Change Alerts"
        val channelDescription = "Notifies when Firebase data changes"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        Log.d("Notification", "Notification channel created")
    }
}