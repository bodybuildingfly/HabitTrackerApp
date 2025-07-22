package com.example.habittrackerapp.util

import android.util.Log
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.model.data.Message
import com.example.habittrackerapp.model.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore

object FirestoreUtil {
    val db = Firebase.firestore

    /**
     * Read data from a specific document path in Firestore
     */
    fun readData(
        collection: String,
        document: String,
        onSuccess: (DocumentSnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collection).document(document)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    onSuccess(document)
                } else {
                    onFailure(Exception("No such document"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Write data to a specific document path in Firestore
     */
    fun writeData(
        collection: String,
        document: String,
        data: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collection)
            .document(document)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Write data to a specific document path in Firestore using a transaction
     */
    fun writeDataWithTransaction(
        collection: String,
        document: String,
        data: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Convert data to a map
        val data = hashMapOf(
            "data" to data,
            "updatedBy" to Firebase.auth.currentUser?.uid,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection(collection)
            .document(document)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /*
     * Get user data from the "users" collection in Firestore
     */
    fun getUserData(
        userId: String = FirebaseUtil.getCurrentUserId() ?: "",
        onDataReceived: (User) -> Unit
    ) {
            if (userId.isNotEmpty()) {
                val docRef = db.collection("users").document(userId)
                docRef.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // Handle error
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d("FirestoreUtil", "Current data: ${snapshot.data}")
                        val userName = snapshot.getString("userName") ?: ""
                        val currentPartner = snapshot.getString("currentPartner") ?: ""
                        val currentPartnership = snapshot.getString("currentPartnership") ?: ""
                        val userData = User(userId, userName, currentPartnership, "")
                        if (currentPartner == "Private") {
                            userData.currentPartner = "Private"
                            onDataReceived(userData)
                        } else {
                            // Get the partner's name from the "users" collection
                            getUserName(currentPartner, onDataReceived = { userName ->
                                userData.currentPartner = userName
                                onDataReceived(userData)
                            })
                        }
                    } else {
                        Log.d("FirestoreUtil", "Current data: null")
                    }
                }
            }
    }

    fun getUserName(
        userId: String,
        onDataReceived: (String) -> Unit
    ) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("userName") ?: ""
                    onDataReceived(userName)
                } else {
                    // Handle error
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    /*
     * Get a list of all partner names from the "users" collection in Firestore
     */
    fun getPartners(
        onSuccess: (List<String>) -> Unit
    ) {
        val currentUser = FirebaseUtil.getCurrentUserId().toString()
        val userRef: DocumentReference = db.collection("users").document(currentUser)
        db.collection("partnership")
            .where(Filter.or(
                Filter.equalTo("user1", userRef),
                Filter.equalTo("user2", userRef)
            ))
            .get()
            .addOnSuccessListener { documents ->
                val partners = mutableListOf<String>()
                for (document in documents) {
                    val partner1 = document.getDocumentReference("user1")
                    val partner2 = document.getDocumentReference("user2")
                    partners += if (userRef == partner1 && partner2 != null) {
                        partner2.toString()
                    } else if (userRef == partner2 && partner1 != null) {
                        partner1.toString()
                    } else {
                        "Private"
                    }
                }
                onSuccess(partners)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    /*
     * Update user data in the "users" collection in Firestore
     */
    fun updateUserData(field: String, value: Any, onComplete: () -> Unit = {}) {
        val docRef = db.collection("users").document(Firebase.auth.currentUser?.uid ?: "")
        docRef.update(field, value)
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    /*
     * Get messages from the "messages" collection in Firestore and sort by timestamp
     */
    fun getMessages(onDataReceived: (List<Message>) -> Unit) {
        val docRef = db.collection("messages")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            val messages = mutableListOf<Message>()
            for (doc in snapshot!!) {
                val message = doc.toObject(Message::class.java)
                messages.add(message)
            }
            messages.sortBy { it.timestamp }
            onDataReceived(messages)
        }
    }

    /**
     * Add a message to the "messages" collection in Firestore
     */
    fun addMessage(message: Message, onComplete: () -> Unit) {
        db.collection("messages")
            .add(message)
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    /**
     * Get habits from the "habits" collection in Firestore
     */
    fun getHabits(onDataReceived: (List<Habit>) -> Unit) {
        val docRef = db.collection("habits")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            val habits = mutableListOf<Habit>()
            for (doc in snapshot!!) {
                val habit = doc.toObject(Habit::class.java)
                // Set the ID of the habit
                habit.id = doc.id
                habits.add(habit)
            }
            onDataReceived(habits)
        }
    }
}