package com.example.habittrackerapp.model.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.model.data.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HabitViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> get() = _habits

    private var habitsListener: ListenerRegistration? = null

    fun fetchHabits() {
        habitsListener?.remove() // Remove any existing listener to prevent duplicates

        habitsListener = db
            .collection("habits")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HabitViewModel", "Error fetching habits", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val habitList = snapshot.documents.mapNotNull { it.toObject(Habit::class.java)?.apply { id = it.id } }
                    _habits.value = habitList
                }
            }
    }

    fun getHabitById(habitId: String): Habit? {
        return _habits.value?.find { it.id == habitId }
    }

    fun updateHabitField(habitId: String, field: String, value: Any) {
        val habit = _habits.value?.find { it.id == habitId }
        habit?.let {
            when (field) {
                "name" -> it.name = value as String
                "description" -> it.description = value as String
                "timesToComplete" -> it.timesToComplete = value as Int
                "timesCompleted" -> it.timesCompleted = value as Int
                "frequency" -> it.frequency = value as Int
                "daysOfWeek" -> it.daysOfWeek = value as String
                else -> Log.e("HabitViewModel", "Invalid field: $field")
            }
        }
    }

    fun updateHabitCounter(habitId: String, newCount: Int) {
        val habit = _habits.value?.find { it.id == habitId }
        habit?.let {
            it.timesCompleted = newCount
            updateHabit(it)
        }
    }

    fun addHabit(habit: Habit) {
        db.collection("habits")
            .add(habit)
            .addOnSuccessListener { Log.d("HabitViewModel", "Habit added successfully") }
            .addOnFailureListener { e -> Log.e("HabitViewModel", "Error adding habit", e) }
    }

    fun updateHabit(habit: Habit) {
        if (habit.id.isNotEmpty()) {
            db.collection("habits")
                .document(habit.id)
                .set(habit)
                .addOnSuccessListener { Log.d("HabitViewModel", "Habit updated successfully") }
                .addOnFailureListener { e -> Log.e("HabitViewModel", "Error updating habit", e) }
        }
    }

    fun deleteHabit(habitId: String) {
        db.collection("habits")
            .document(habitId)
            .delete()
            .addOnSuccessListener { Log.d("HabitViewModel", "Habit deleted successfully") }
            .addOnFailureListener { e -> Log.e("HabitViewModel", "Error deleting habit", e) }
    }

    override fun onCleared() {
        super.onCleared()
        habitsListener?.remove() // Clean up Firestore listener when ViewModel is cleared
    }
}