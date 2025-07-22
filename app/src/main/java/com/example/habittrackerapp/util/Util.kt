package com.example.habittrackerapp.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Util {

    // Convert timestamp to a more readable format
    fun convertTimestamp(timestamp: Long): String {
        val date = Date.from(epochSecondsToLocalDateTime(timestamp).toInstant(ZoneOffset.UTC))
        val calendar = Calendar.getInstance()
        calendar.time = date
        val currentTime = Calendar.getInstance()

        return when {
            // Today
            calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR) -> {
                "Today " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            }
            // Yesterday
            calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR) - 1 -> {
                "Yesterday " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            }
            // If within 7 days, output the day of the week
            calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) - 7 <= currentTime.get(Calendar.DAY_OF_YEAR) -> {
                        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
                dayOfWeek + " " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            }
            // Older messages
            else -> {
                SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(date)
            }
        }
    }

    fun epochSecondsToLocalDateTime(epochSeconds: Long): LocalDateTime {
        val instant = Instant.ofEpochSecond(epochSeconds)
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }
}