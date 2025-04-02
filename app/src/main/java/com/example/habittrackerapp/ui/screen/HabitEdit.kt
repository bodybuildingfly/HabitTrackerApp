package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.util.FirebaseUtil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitEdit(
    modifier: Modifier,
    habitId: String,
    onClose: () -> Unit
) {
    val habit = remember { mutableStateOf(Habit(), neverEqualPolicy()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load habit data once when habitId changes
    LaunchedEffect(habitId) {
        Log.d("HabitEdit", "Loading habit data for habitId: $habitId")
        FirebaseUtil.readData("habits/$habitId",
            onDataReceived = {
                habit.value = it.getValue(Habit::class.java) ?: Habit()
                isLoading = false
            },
            onFailure = {
                Log.e("HabitEdit", "Error reading data", it.toException())
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { /* No title needed */ },
                // Chat icon button on the right side
                actions = {
                    IconButton(onClick = {
                        onClose()
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
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

                // Section 1 - Name and description
                Section1(
                    name = habit.value.name,
                    description = habit.value.description,
                    onNameChange = {
                        habit.value = habit.value.copy(name = it)
                    },
                    onDescriptionChange = {
                        habit.value = habit.value.copy(description = it)
                    }
                )
                Spacer(modifier = Modifier.padding(16.dp))

                // Section 2 - Times to complete
                Section2(
                    timesToComplete = habit.value.timesToComplete,
                    onTimesToCompleteChange = {
                        habit.value = habit.value.copy(timesToComplete = it)
                    }
                )
                Spacer(modifier = Modifier.padding(16.dp))

                // Section 3 - Frequency
                Section3(
                    frequency = habit.value.frequency,
                    daysOfWeek = habit.value.daysOfWeek,
                    onFrequencyChange = {
                        habit.value = habit.value.copy(frequency = it)
                    },
                    onDaysOfWeekChange = {
                        habit.value = habit.value.copy(daysOfWeek = it)
                    }
                )
                Spacer(modifier = Modifier.padding(16.dp))

                // Bottom save button
                Button(
                    modifier = modifier,
                    onClick = {
                        // Save habit data to Firebase
                        FirebaseUtil.writeData(
                            "habits/${habitId}", habit.value, onSuccess = {
                                onClose()
                            }, onFailure = {
                                Log.e("HabitEdit", "Error writing data", it)
                            }
                        )
                    },
                    content = {
                        Text(text = "Save")
                    }
                )
            }
        }
    }
}

@Composable
fun Section1(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Text(
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        text = "What is the name of your habit?"
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        style = MaterialTheme.typography.bodySmall,
        text = "Habit name"
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        value = name,
        onValueChange = {
            onNameChange(it)
        }
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        style = MaterialTheme.typography.bodySmall,
        text = "Habit description"
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        value = description,
        onValueChange = {
            onDescriptionChange(it)
        }
    )
}

@Composable
fun Section2(
    timesToComplete: Int,
    onTimesToCompleteChange: (Int) -> Unit
) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableIntStateOf(0) }
    val timesToCompleteOptions = listOf("At least")
    val numbersOnlyTwoDigitsPattern = remember { Regex("\\d{0,2}(\\.\\d{1,2})?") }

    Text(
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        text = "How often should this task be completed?"
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Box {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isDropDownExpanded.value = true
                }
        ) {
            Text(text = timesToCompleteOptions[itemPosition.intValue])
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "DropDown Icon"
            )
            TextField(
                value = timesToComplete.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text(text = if (timesToComplete == 1) "time" else "times")
                },
                onValueChange = {
                    if (it.matches(numbersOnlyTwoDigitsPattern)) {
                        onTimesToCompleteChange(if (it.isEmpty()) 0 else it.toInt())
                    }
                }
            )
        }
        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = {
                isDropDownExpanded.value = false
            }) {
            timesToCompleteOptions.forEachIndexed { index, option ->
                DropdownMenuItem(text = {
                    Text(text = option)
                },
                    onClick = {
                        isDropDownExpanded.value = false
                        itemPosition.intValue = index
                    })
            }
        }
    }
}


@Composable
fun Section3(
    frequency: Int,
    daysOfWeek: String,
    onFrequencyChange: (Int) -> Unit,
    onDaysOfWeekChange: (String) -> Unit
) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableIntStateOf(frequency) }
    val frequencyOptions = listOf("Once", "Daily", "On selected weekdays", "Weekly", "Monthly")
    var daysOfWeek by remember { mutableStateOf(daysOfWeek) }

    // Toggle day in daysOfWeek
    fun toggleDay(index: Int) {
        daysOfWeek = daysOfWeek.toMutableList().apply {
            this[index] = if (this[index] == '1') '0' else '1'
        }.joinToString("")
        Log.d("Section3", "New daysOfWeek: $daysOfWeek")
    }

    Text(
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        text = "How often should this task repeat?"
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Box {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isDropDownExpanded.value = true
                }
        ) {
            Text(text = frequencyOptions[itemPosition.intValue])
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "DropDown Icon"
            )
        }
        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = {
                isDropDownExpanded.value = false
            }) {
            frequencyOptions.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option)
                    },
                    onClick = {
                        isDropDownExpanded.value = false
                        itemPosition.intValue = index
                    })
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DayOfWeekIcon( enabled = daysOfWeek[0] == '1', text = "S", onClick = { toggleDay(0)})
        DayOfWeekIcon( enabled = daysOfWeek[1] == '1', text = "M", onClick = { toggleDay(1)})
        DayOfWeekIcon( enabled = daysOfWeek[2] == '1', text = "T", onClick = { toggleDay(2)})
        DayOfWeekIcon( enabled = daysOfWeek[3] == '1', text = "W", onClick = { toggleDay(3)})
        DayOfWeekIcon( enabled = daysOfWeek[4] == '1', text = "T", onClick = { toggleDay(4)})
        DayOfWeekIcon( enabled = daysOfWeek[5] == '1', text = "F", onClick = { toggleDay(5)})
        DayOfWeekIcon( enabled = daysOfWeek[6] == '1', text = "S", onClick = { toggleDay(6)})
    }
}

@Composable
fun DayOfWeekIcon(
    enabled: Boolean = true,
    text: String = "",
    onClick: () -> Unit
) {
    var enabled by remember { mutableStateOf(enabled) }

    Surface(
        modifier = Modifier,
        shape = CircleShape,
        onClick = {
            enabled = !enabled
            onClick()
        }
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(36.dp),
                imageVector = Icons.Filled.Circle,
                contentDescription = "Day of week selection",
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}