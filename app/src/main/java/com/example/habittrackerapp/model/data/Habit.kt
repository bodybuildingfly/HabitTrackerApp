package com.example.habittrackerapp.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habit(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var frequency: Int = 0,
    var daysOfWeek: String = "0000000",
    var dayOfMonth: Int = 0,
    var timesToComplete: Int = 0,
    var timesCompleted: Int = 0,
    var nextDueDate: Long = 0,
    var stability: Int = 0 // Not sure what this is, but the db appears to have created it
) : Parcelable