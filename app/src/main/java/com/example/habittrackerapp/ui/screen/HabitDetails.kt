package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.components.MenuItem
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.util.FirebaseUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetails(
    modifier: Modifier = Modifier,
    habitId: String,
    onClose: () -> Unit,
    onReminder: () -> Unit,
    onViewHistory: () -> Unit,
    onPause: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val habit = remember { mutableStateOf(Habit(), neverEqualPolicy()) }
    var isLoading by remember { mutableStateOf(true) }
    var counter by remember { mutableIntStateOf(0) }

    Log.d("HabitDetails", "Recomposing with habit: $habitId")

    // Load habit data once when habitId changes
    LaunchedEffect(habitId) {
        FirebaseUtil.readData("habits/$habitId",
            onDataReceived = {
                habit.value = it.getValue(Habit::class.java) ?: Habit()
                counter = habit.value.timesCompleted
                isLoading = false
            },
            onFailure = {
                Log.e("HabitDetails", "Error reading data", it.toException())
            }
        )
    }

    // Update counter in database when it changes
    LaunchedEffect(counter) {
        FirebaseUtil.writeData("habits/$habitId/timesCompleted", counter,
            onSuccess = { /* Success, no action needed */ },
            onFailure = { Log.e("EditNotes", "Error writing data", it) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { /* No title needed */ },
                actions = {
                    HabitDropdownMenu(
                        onReminder = onReminder,
                        onViewHistory = onViewHistory,
                        onPause = onPause,
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .padding(horizontal = 24.dp)
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        modifier = Modifier.weight(.65F),
                        text = habit.value.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    CounterSection(
                        counter, habit.value.timesToComplete
                    ) { newCount ->
                        counter = newCount
                    }
                }
                Text(
                    text = habit.value.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This habit must be done at least once every day.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CounterSection(counter: Int, maxCount: Int, onCounterChange: (Int) -> Unit) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { if (counter > 0) onCounterChange(counter - 1) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrement")
        }
        Text(
            text = "$counter/$maxCount",
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = { onCounterChange(counter + 1) }) {
            Icon(Icons.Filled.Add, contentDescription = "Increment")
        }
    }
}

@Composable
fun HabitDropdownMenu(
    onReminder: () -> Unit,
    onViewHistory: () -> Unit,
    onPause: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        MenuItem("Reminder", Icons.Filled.AddAlert, onReminder)
        MenuItem("View history", Icons.Filled.History, onViewHistory)
        MenuItem("Pause", Icons.Outlined.Pause, onPause)
        MenuItem("Edit", Icons.Outlined.Edit, onEdit)
        MenuItem("Delete", Icons.Outlined.Delete, onDelete)
    }
}

@Composable
fun MenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(title) },
        leadingIcon = { Icon(icon, contentDescription = title) },
        onClick = onClick
    )
}