package com.example.habittrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import com.example.habittrackerapp.ui.screen.AppScreen
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.util.FirebaseUtil
import com.example.habittrackerapp.util.PermissionUtil

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerAppTheme {
                // Get the click action from the intent (Typically passed by the notification)
                val clickAction = intent?.getStringExtra("click_action")

                // Request notification permission
                val context = LocalContext.current
                PermissionUtil.NotificationPermissionRequest(context)

                // Get the FCM token
                FirebaseUtil.getFCMToken()

                // Open the home screen
                AppScreen(clickAction = clickAction)
            }
        }
    }
}
