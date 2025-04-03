package com.example.habittrackerapp.util

import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.model.data.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

object FirestoreUtil {
    val db = Firebase.firestore

    /**
     * Read data from a specific document path in Firestore
     */
    fun readData(
        collection: String, document: String,
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