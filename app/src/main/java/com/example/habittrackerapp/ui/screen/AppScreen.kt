package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.util.FirebaseUtil

@Composable
fun AppScreen(
    clickAction: String?
) {
    val mainNavController = rememberNavController()

    // If click_action is provided, navigate to the corresponding screen
    LaunchedEffect(clickAction) {
        Log.d("AppScreen", "Recomposing with clickAction: $clickAction")
        when (clickAction) {
            "OPEN_CHAT_SCREEN" -> {
                Log.d("AppScreen", "Opening Chat Screen")
                // Navigate to Chat Screen
                mainNavController.navigate("Chat")
            }
            else -> {
                // Navigate to Home Screen
                mainNavController.navigate("Home")
            }
        }
    }

    NavHost(
        navController = mainNavController,
        startDestination = if (FirebaseUtil.isLoggedIn()) "Home" else "Login"
    ) {
        composable("Login") {
            LoginScreen(
                onLoginSuccess = {
                    mainNavController.navigate("Home")
                }
            )
        }
        composable("Home") {
            HomeScreen(
                mainNavController = mainNavController
            )
        }
        composable("Chat") {
            ChatScreen(
                onClose = mainNavController::popBackStack
            )
        }
        composable("EditNotes/{tab}") { backStackEntry ->
            EditNotes(
                tab = backStackEntry.arguments?.getString("tab") ?: "",
                onClose = {
                    mainNavController.popBackStack()
                }
            )
        }
//        composable("HabitDetails") {
//            val habit = mainNavController.previousBackStackEntry?.savedStateHandle?.get<Habit>("habit")
//            habit?.let {
//                HabitDetails(
//                    habit = it,
//                    onClose = { mainNavController.popBackStack() },
//                    onReminder = { /* Handle reminder */ },
//                    onViewHistory = { /* Handle view history */ },
//                    onPause = { /* Handle pause */ },
//                    onEdit = { /* Handle edit */ },
//                    onDelete = { /* Handle delete */ }
//                )
//            }
//        }
        composable("HabitDetails/{habitId}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            HabitDetails(
                habitId = habitId,
                onClose = { mainNavController.popBackStack() },
                onReminder = { /* Handle reminder */ },
                onViewHistory = { /* Handle view history */ },
                onPause = { /* Handle pause */ },
                onEdit = { mainNavController.navigate("HabitEdit/$habitId") },
                onDelete = { /* Handle delete */ }
            )
        }
        composable("HabitEdit/{habitId}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            HabitEdit(
                modifier = Modifier,
                habitId = habitId,
                onClose = { mainNavController.popBackStack() },
            )
        }
    }
}