package com.example.habittrackerapp.util

import android.util.Log
import com.example.habittrackerapp.model.data.Habit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    /**
     * Write data to a specific path in the database
     */
    fun writeData(path: String, data: Any, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getDatabaseRef(path).setValue(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Read data from a specific path in the database
     */
    fun readData(path: String, onDataReceived: (DataSnapshot) -> Unit, onFailure: (DatabaseError) -> Unit) {
        getDatabaseRef(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataReceived(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }

    /**
     * Listen for real-time updates at multiple paths
     */
    fun listenForUpdates(paths: List<String>, onDataChanged: (String, DataSnapshot) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        paths.forEach { path ->
            val ref = getDatabaseRef(path)
            var isInitialLoad = true // Flag to track first-time load for each path

            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isInitialLoad) {
                        isInitialLoad = false
                        return // Skip initial load
                    }

                    // Extract the 'updatedBy' field to check who made the change
                    val updatedBy = snapshot.child("updatedBy").getValue(String::class.java)

                    // Ignore notifications if the update was made by the current user
                    if (updatedBy == currentUserId) {
                        return
                    }

                    // Pass the path along with the snapshot
                    onDataChanged(path, snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error (consider logging per path)
                }
            })
        }
    }

    // Function to fetch habits
    fun getHabits(onDataReceived: (List<Habit>) -> Unit) {
        database.getReference("habits").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val habits = snapshot.children.mapNotNull { it.getValue(Habit::class.java) }
                // Add id to each habit using the key of the snapshot
                habits.forEachIndexed { index, habit ->
                    habit.id = snapshot.children.elementAt(index).key ?: ""
                }
                // Return the list of habits
                onDataReceived(habits)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtil", "Error fetching habits", error.toException())
            }
        })
    }

    // Function to fetch a single habit by ID
    fun getHabitById(
        habitId: String,
        onDataReceived: (Habit) -> Unit
    ) {
        database.getReference("habits").child(habitId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val habit = snapshot.getValue(Habit::class.java)
                habit?.let { onDataReceived(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtil", "Error fetching habit with id $habitId - ", error.toException())
            }
        })
    }

    // Function to add a new habit
    fun addHabit(habit: Habit, onComplete: (Boolean) -> Unit) {
        val habitId = database.getReference("habits").push().key ?: ""
        val habitWithId = habit.copy(id = habitId)
        database.getReference("habits").child(habitId).setValue(habitWithId)
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }

    // Function to delete a habit
    fun deleteHabit(habitId: String, onComplete: (Boolean) -> Unit) {
        database.getReference("habits").child(habitId).removeValue()
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }

    // Function to update a habit's counter
    fun updateHabitCounter(habitId: String, counter: Int, onComplete: (Boolean) -> Unit) {
        database.getReference("habits").child(habitId).child("timesCompleted").setValue(counter)
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
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
}

