package com.example.habittrackerapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import com.example.habittrackerapp.model.view.AppViewModel
import com.example.habittrackerapp.ui.screen.AppScreen
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.util.FirebaseUtil
import com.example.habittrackerapp.util.PermissionUtil


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerAppTheme {
                // Get the click action from the intent (Typically passed by the notification)
                val clickAction = intent?.getStringExtra("click_action")
                Log.d("MainActivity", "Click Action: $clickAction")

                // Request permissions
                val context = LocalContext.current
                PermissionUtil.RequestPermission(context, Manifest.permission.POST_NOTIFICATIONS, {}, {})

                // Create the notification channel
                FirebaseUtil.createNotificationChannel(context)

                // Get the FCM token
                FirebaseUtil.getFCMToken()

                // Open the home screen
                AppScreen(
                    clickAction = clickAction
                )
            }
        }
    }
}
