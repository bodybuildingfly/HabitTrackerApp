package com.example.habittrackerapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.model.view.HabitViewModel
import com.example.habittrackerapp.ui.navigation.MainBottomBar
import com.example.habittrackerapp.ui.navigation.MainTopBar
import com.example.habittrackerapp.util.FirebaseUtil

@Composable
fun HomeScreen(
    mainNavController: NavController,
    habitViewModel: HabitViewModel,
    page: String = "Habits",
    tab: String = ""
) {
    val homeNavController = rememberNavController()

    // Determine the start destination based on the provided arguments
    val startDestination = if (tab.isNotEmpty()) { "$page/$tab" } else { page }

    Scaffold(
        topBar = {
            MainTopBar(
                modifier = Modifier,
                title = "",
                onProfile = { mainNavController.navigate("Profile") },
                onPartners = { mainNavController.navigate("Partners") },
                onSettings = { mainNavController.navigate("Settings") },
                onLogout = {
                    FirebaseUtil.logout()
                    mainNavController.navigate("Login")
                },
                onChat = { mainNavController.navigate("Chat") },
            )
        },
        bottomBar = {
            MainBottomBar(
                navController = homeNavController,
                modifier = Modifier
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding to the entire column
                .consumeWindowInsets(paddingValues) // Consume the padding
        ) {

            NavHost(
                navController = homeNavController,
                startDestination = startDestination
            ) {
                composable("Habits") {
                    HabitsList(
                        onHabitClick = { habitId ->
                            mainNavController.navigate("HabitDetails/$habitId")
                        },
                        habitViewModel = habitViewModel
                    )
                }

                composable("Journal") {
                    JournalList()
                }
                composable("Notes") {
                    NotesScreen { tab ->
                        mainNavController.navigate("EditNotes/$tab")
                    }
                }
                composable("Notes/{tab}") {
                    NotesScreen(
                        tab = it.arguments?.getString("tab") ?: "",
                    ) { tab ->
                        mainNavController.navigate("EditNotes/$tab")
                    }
                }
            }
        }
    }
}