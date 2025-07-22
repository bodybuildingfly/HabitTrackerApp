package com.example.habittrackerapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseUtil {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Get a reference to a specified path in the Realtime Database
     */
    fun getDatabaseRef(path: String): DatabaseReference {
        return database.getReference(path)
    }

    /**
     * Get the current user's UID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Log in with the provided email and password
     */
    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Log out the current user
     */
    fun logout() {
        auth.signOut()
    }

    // Function to get the FCM token and save it to Firebase
    fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM Token", "Token: $token")

                    // Save the token in Firebase under the user's profile
                    saveTokenToFirestore(token)
                } else {
                    Log.w("FCM Token", "Fetching FCM token failed", task.exception)
                }
            }
    }

    fun saveTokenToFirestore(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()

            val userRef = db.collection("users").document(userId)

            userRef.set(mapOf("token" to token), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("FirestoreUtil", "Token saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUtil", "Error saving token", e)
                }
        }
    }

    fun createNotificationChannel(context: Context) {
        val channelId = "@string/default_notification_channel_id"
        val channelName = "@string/default_notification_channel_name"
        val channelDescription = "@string/default_notification_channel_description"
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

