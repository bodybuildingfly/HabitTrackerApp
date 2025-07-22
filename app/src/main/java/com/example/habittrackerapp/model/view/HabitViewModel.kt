package com.example.habittrackerapp.model.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.model.data.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

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

                    // Sort habits by next due date
                    habitList.sortedBy { it.nextDueDate }

                    // Group habits by next due date
                    val groupedHabits = habitList.groupBy { it.nextDueDate }

                    // Sort habits within each group by name
                    groupedHabits.forEach { (_, habits) ->
                        habits.sortedBy { it.name }
                    }

                    Log.d("HabitViewModel", "Habits fetched successfully")
                    Log.d("HabitViewModel", "Habits: ${groupedHabits}")

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
            val nextDueDate = calculateNextDueDate(habit)
            habit.nextDueDate = nextDueDate
            Log.d("HabitViewModel", "Next due date: $nextDueDate")
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

    fun calculateNextDueDate(habit: Habit): Long {
        val today = LocalDateTime.now()
        val timeZone = today.atZone(java.time.ZoneId.systemDefault()).offset
        val endOfToday = setToEndOfDay(today)
        return when (habit.frequency) {
            0 -> endOfToday.toInstant(timeZone).epochSecond.toLong() // Daily
            1 -> calculateNextDueDateForWeekdays(habit.daysOfWeek, endOfToday, timeZone) // On selected weekdays
            2 -> calculateNextDueDateForWeekdays(habit.daysOfWeek, endOfToday, timeZone) // Weekly
            3 -> calculateNextDueDateForMonth(habit.dayOfMonth, endOfToday, timeZone) // Monthly
            else -> endOfToday.toInstant(timeZone).epochSecond.toLong()
        }
    }

    private fun calculateNextDueDateForWeekdays(daysOfWeek: String, today: LocalDateTime, timeZone: java.time.ZoneOffset): Long {
        // Get calendar instance
        val calendar = Calendar.getInstance()

        // Set calendar to today's day of the week (Integer)
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        // Reorder daysOfWeek to start with today's day
        val daysOfWeekStartingWithToday = daysOfWeek.substring(day - 1) + daysOfWeek.substring(0, day - 1)

        // Find the first selected day of the week starting with today
        val nextSelectedDay = daysOfWeekStartingWithToday.indexOf('1')

        // Calculate the next due date
        val nextDueDate = today.plusDays(nextSelectedDay.toLong())

        return nextDueDate.toInstant(timeZone).epochSecond.toLong()
    }

    private fun calculateNextDueDateForMonth(dayOfMonth: Int, today: LocalDateTime, timeZone: java.time.ZoneOffset): Long {
        // Set the next due date to today
        var nextDueDate = today

        // Get the current day of the month
        val currentDayOfMonth = today.dayOfMonth

        // Calculate the next due date based on the day of the month
        if (dayOfMonth == 0 && currentDayOfMonth > 1) {
            val nextMonth = today.plusMonths(1)
            nextDueDate = nextMonth.withDayOfMonth(1)
        } else if (dayOfMonth == 1) {
            if (currentDayOfMonth > 15) {
                val nextMonth = today.plusMonths(1)
                nextDueDate = nextMonth.withDayOfMonth(15)
            } else {
                nextDueDate = today.withDayOfMonth(15)
            }
        }
        return nextDueDate.toInstant(timeZone).epochSecond.toLong()
    }

    private fun setToEndOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.with(LocalTime.MAX).truncatedTo(ChronoUnit.MINUTES)
    }

    override fun onCleared() {
        super.onCleared()
        habitsListener?.remove() // Clean up Firestore listener when ViewModel is cleared
    }
}