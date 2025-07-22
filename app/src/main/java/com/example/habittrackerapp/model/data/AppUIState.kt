package com.example.habittrackerapp.model.data

data class AppUIState(
    val loggedIn: Boolean = false,
    val user: User? = null,
)