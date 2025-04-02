package com.example.habittrackerapp.model.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.util.FirebaseUtil

class HabitViewModel : ViewModel() {
    private val _habits = mutableStateOf<List<Habit>>(emptyList())
    val habits: State<List<Habit>> = _habits

    init {
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.getHabits { habitList ->
                _habits.value = habitList
            }
        }
    }
    // Get habits function
    fun getHabits(onDataReceived: (List<Habit>) -> Unit) {
        FirebaseUtil.getHabits(onDataReceived)
    }

    // Get habit by ID function
    fun getHabitById(habitId: String, onDataReceived: (Habit) -> Unit) {
        FirebaseUtil.getHabitById(habitId, onDataReceived)
    }

    // Add habit function
    fun addHabit(habit: Habit, onComplete: (Boolean) -> Unit) {
        FirebaseUtil.addHabit(habit, onComplete)
    }

    // Delete habit function
    fun deleteHabit(habitId: String, onComplete: (Boolean) -> Unit) {
        FirebaseUtil.deleteHabit(habitId, onComplete)
    }
}