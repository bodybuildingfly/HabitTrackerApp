package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.model.view.HabitViewModel
import com.example.habittrackerapp.util.FirebaseUtil

@Composable
fun AppScreen(
    clickAction: String?
) {
    val mainNavController = rememberNavController()

    val habitViewModel = remember { HabitViewModel() }

    // If click_action is provided, navigate to the corresponding screen
    LaunchedEffect(clickAction) {
        Log.d("AppScreen", "Recomposing with clickAction: $clickAction")
        if (clickAction != null) {
            mainNavController.navigate(clickAction)
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
                mainNavController = mainNavController,
                habitViewModel = habitViewModel
            )
        }
        composable("Home/{page}") {
            HomeScreen(
                mainNavController = mainNavController,
                habitViewModel = habitViewModel,
                page = it.arguments?.getString("page") ?: ""
            )
        }
        composable("Home/{page}/{tab}") {
            HomeScreen(
                mainNavController = mainNavController,
                habitViewModel = habitViewModel,
                page = it.arguments?.getString("page") ?: "",
                tab = it.arguments?.getString("tab") ?: ""
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
        composable("HabitDetails/{habitId}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            HabitDetails(
                modifier = Modifier,
                habit = habitViewModel.getHabitById(habitId) ?: Habit(),
                onCounterChange = {
                    habitViewModel.updateHabitCounter(habitId, it)
                },
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
                habit = (habitViewModel.getHabitById(habitId) ?: Habit()),
                onClose = { mainNavController.popBackStack() },
                onSave = {
                    habitViewModel.updateHabit(it)
                    mainNavController.popBackStack()
                }
            )
        }
        composable("Profile") {
//                    ProfileScreen()
        }
        composable("Partners") {
//                    PartnersScreen()
        }
        composable("Settings") {
//                    SettingsScreen()
        }
    }
}