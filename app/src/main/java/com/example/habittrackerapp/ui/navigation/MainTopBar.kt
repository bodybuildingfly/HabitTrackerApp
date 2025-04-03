package com.example.habittrackerapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
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
import com.example.habittrackerapp.components.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(
    modifier: Modifier,
    title: String,
    onProfile: () -> Unit,
    onPartners: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    onChat: () -> Unit,
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
            HomeDropdownMenu(
                onProfile = onProfile,
                onPartners = onPartners,
                onSettings = onSettings,
                onLogout = onLogout
            )
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


@Composable
fun HomeDropdownMenu(
    onProfile: () -> Unit,
    onPartners: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Filled.Menu, contentDescription = "Menu")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        MenuItem("Profile", Icons.Filled.Person, onProfile)
        MenuItem("Partners", Icons.Filled.PeopleAlt, onPartners)
        MenuItem("Settings", Icons.Outlined.Settings, onSettings)
        HorizontalDivider()
        MenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, onLogout)
    }
}