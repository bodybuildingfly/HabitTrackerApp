package com.example.habittrackerapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.ui.navigation.MainBottomBar
import com.example.habittrackerapp.ui.navigation.MainTopBar
import com.example.habittrackerapp.util.FirebaseUtil

@Composable
fun HomeScreen(
    mainNavController: NavController
) {

    val homeNavController = rememberNavController()

    Scaffold(
        topBar = {
            MainTopBar(
                title = "",
                onNotification = { /* Handle notifications */ },
                onLogout = {
                    FirebaseUtil.logout()
                    mainNavController.navigate("Login")
                },
                onChat = { mainNavController.navigate("Chat") },
                modifier = Modifier
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
//                .windowInsetsPadding(WindowInsets.ime) // Keep the top bar in place when keyboard opens
//                .imePadding() // Keeps the content going all the way to the bottom of the available screen
        ) {

            NavHost(
                navController = homeNavController,
                startDestination = "Habits"
            ) {
//                composable("Habits") {
//                    Log.d("HomeScreen", "Loading Habits")
//                    HabitsList(onHabitClick = { habit ->
//                        mainNavController.currentBackStackEntry?.savedStateHandle?.set("habit", habit)
//                        mainNavController.navigate("HabitDetails")
//                    })
//                }
                composable("Habits") {
                    HabitsList(onHabitClick = { habitId ->
                        mainNavController.navigate("HabitDetails/$habitId")
                    })
                }

                composable("HabitEdit") {
//            HabitEdit()
                }

                composable("Journal") {
                    JournalList()
                }

                composable("Notes") {
                    NotesScreen { tab ->
                        mainNavController.navigate("EditNotes/$tab")
                    }
                }
            }
        }
    }
}