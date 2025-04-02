package com.example.habittrackerapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(
    modifier: Modifier,
    title: String,
    onLogout: () -> Unit,
    onChat: () -> Unit,
    onNotification: () -> Unit
){
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = title
            )
        },
        navigationIcon = {
            // Remember the state of the dropdown menu
            var expanded by remember { mutableStateOf(false) }

            // Hamburger icon to show dropdown menu
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }

            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Profile menu item
                DropdownMenuItem(
                    text = { Text("Profile") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    onClick = { /* Do something... */ }
                )
                // Settings menu icon
                DropdownMenuItem(
                    text = { Text("Settings") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    },
                    onClick = { /* Do something... */ }
                )

                HorizontalDivider()

                // Test notification menu item
                DropdownMenuItem(
                    text = { Text("Test Notification") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Feedback,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onNotification()
                    }
                )

                HorizontalDivider()

                // Log out menu item
                DropdownMenuItem(
                    text = { Text("Log out") },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Log out"
                        )
                    },
                    onClick = {
                        onLogout()
                    }
                )
            }
        },

        // Chat icon button on the right side
        actions = {
            IconButton(onClick = {
                onChat()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat"
                )
            }
        }
    )
}