package com.example.habittrackerapp.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habit(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: Int = 0,
    val daysOfWeek: String = "0000000",
    val dayOfMonth: Int = 0,
    val timesToComplete: Int = 0,
    val timesCompleted: Int = 0,
    val nextDueDate: String? = null
) : Parcelable