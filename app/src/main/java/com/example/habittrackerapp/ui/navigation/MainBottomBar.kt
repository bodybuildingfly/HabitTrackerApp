package com.example.habittrackerapp.ui.navigation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.habittrackerapp.model.data.HomeScreen
import com.example.habittrackerapp.ui.screen.HabitsList
import com.example.habittrackerapp.ui.screen.JournalList
import com.example.habittrackerapp.ui.screen.NotesScreen

@Composable
internal fun MainBottomBar(
    modifier: Modifier,
    navController: NavHostController
) {

    // Define the selected tab index
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    // Observe the current backstack entry
    val backstackEntry by navController.currentBackStackEntryAsState()

    // Update selectedTabIndex when backstack changes
    LaunchedEffect(backstackEntry) {
        selectedTabIndex = getSelectedTabIndex(navController)
    }

    NavigationBar(
        modifier = Modifier
            .heightIn(max = 120.dp),
        containerColor = MaterialTheme.colorScheme.tertiary,
    ) {
        // looping over each tab to generate the views and navigation for each item
        homeScreens.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(navItem.route) {
                        launchSingleTop = true  // Prevents duplicate navigation events
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (navItem.badgeAmount != null) {
                                Badge {
                                    Text(navItem.badgeAmount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.route
                        )
                    }
                },
                label = { Text(navItem.route) }
            )
        }
    }
}

// Helper function to determine the selected tab index based on navigation
private fun getSelectedTabIndex(navController: NavController): Int {
    val currentDestination = navController.currentDestination?.route
    return homeScreens.indexOfFirst { it.route == currentDestination }.takeIf { it != -1 } ?: 0
}

// List of home screens with their associated data
val homeScreens = listOf(
    HomeScreen(
        icon = Icons.Filled.Home,
        route = "Habits",
        badgeAmount = null
    ),
    HomeScreen(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        route = "Journal",
        badgeAmount = null
    ),
    HomeScreen(
        icon = Icons.Filled.NoteAlt,
        route = "Notes",
        badgeAmount = null
    )
)