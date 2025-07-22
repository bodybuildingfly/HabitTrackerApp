package com.example.habittrackerapp.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.model.view.AppViewModel
import com.example.habittrackerapp.model.view.HabitViewModel
import com.example.habittrackerapp.ui.bottomsheet.SelectPartner
import com.example.habittrackerapp.ui.navigation.MainBottomBar
import com.example.habittrackerapp.ui.navigation.MainTopBar
import com.example.habittrackerapp.util.FirebaseUtil
import kotlinx.coroutines.launch

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavController,
    appViewModel: AppViewModel,
    habitViewModel: HabitViewModel,
    page: String = "Habits",
    tab: String = ""
) {
    val homeNavController = rememberNavController()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var bottomSheet by remember { mutableStateOf("") }

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
                onPartnerSelect = {
                    showBottomSheet = true
                    bottomSheet = "SelectPartner"
                }
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
                        habitViewModel = habitViewModel,
                        onHabitClick = { habitId ->
                            mainNavController.navigate("HabitDetails/$habitId")
                        },
                        onHabitNew = {
                            mainNavController.navigate("HabitEdit")
                        }
                    )
                }
                composable("Journal") {
                    JournalList(
                        appViewModel = appViewModel
                    )
                }
                composable("Notes") {
                    NotesScreen(
                        appViewModel
                    ) {
                        mainNavController.navigate("NotesEdit")
                    }
                }
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    when (bottomSheet) {
                        "SelectPartner" -> {
                            SelectPartner(
                                onDismiss = {
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}