package com.example.habittrackerapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.ui.navigation.SaveButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEdit(
    modifier: Modifier,
    habit: Habit,
    onClose: () -> Unit,
    onSave: (Habit) -> Unit
) {

    var habit by remember { mutableStateOf(habit) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { /* No title needed */ },
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
        Column(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            // Section 1 - Name and description
            Section1(
                name = habit.name,
                description = habit.description,
                onNameChange = {
                    habit = habit.copy(name = it)
                },
                onDescriptionChange = {
                    habit = habit.copy(description = it)
                }
            )
            Spacer(modifier = Modifier.padding(16.dp))

            // Section 2 - Times to complete
            Section2(
                timesToComplete = habit.timesToComplete,
                onTimesToCompleteChange = {
                    habit = habit.copy(timesToComplete = it)
                }
            )
            Spacer(modifier = Modifier.padding(16.dp))

            // Section 3 - Frequency
            Section3(
                frequency = habit.frequency,
                daysOfWeek = habit.daysOfWeek,
                dayOfMonth = habit.dayOfMonth,
                onFrequencyChange = {
                    habit = habit.copy(frequency = it)
                },
                onDaysOfWeekChange = {
                    habit = habit.copy(daysOfWeek = it)
                },
                onDayOfMonthChange = {
                    habit = habit.copy(dayOfMonth = it)
                }
            )
            Spacer(modifier = Modifier.padding(16.dp))

            // Bottom save button
            SaveButton(
                onSave = { onSave(habit) }
            )
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
    dayOfMonth: Int,
    onFrequencyChange: (Int) -> Unit,
    onDaysOfWeekChange: (String) -> Unit,
    onDayOfMonthChange: (Int) -> Unit
) {
    val isFrequencyDropDownExpanded = remember { mutableStateOf(false) }
    val frequencyPosition = remember { mutableIntStateOf(frequency) }
    val frequencyOptions = listOf("Daily", "On selected weekdays", "Weekly", "Monthly")

    var daysOfWeek by remember { mutableStateOf(daysOfWeek) }

    val isDayOfMonthDropDownExpanded = remember { mutableStateOf(false) }
    val dayOfMonthPosition = remember { mutableIntStateOf(dayOfMonth) }
    val daysOfMonthOptions = listOf("On the first of the month", "On the 15th of the month")

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
                    isFrequencyDropDownExpanded.value = true
                }
        ) {
            Text(text = frequencyOptions[frequencyPosition.intValue])
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "DropDown Icon"
            )
        }
        DropdownMenu(
            expanded = isFrequencyDropDownExpanded.value,
            onDismissRequest = {
                isFrequencyDropDownExpanded.value = false
            }) {
            frequencyOptions.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option)
                    },
                    onClick = {
                        isFrequencyDropDownExpanded.value = false
                        frequencyPosition.intValue = index
                        onFrequencyChange(index)
                    })
            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))

    // Show additional content based on the selected option
    if (frequencyPosition.intValue == 2) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            text = "By which day of the week is this habit due?"
        )
    }

    // Show DayOfWeekSelector when frequency option is selected
    if (frequencyPosition.intValue == 1 || frequencyPosition.intValue == 2) {
        DayOfWeekSelector(
            daysOfWeek = daysOfWeek,
            singleDaySelectable = frequencyPosition.intValue == 2,
            onDaysOfWeekChange = {
                onDaysOfWeekChange(it)
            }
        )
    }

    // Show DayOfMonthSelector when frequency option is selected
    if (frequencyPosition.intValue == 3) {
        Box {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isDayOfMonthDropDownExpanded.value = true
                    }
            ) {
                Text(text = daysOfMonthOptions[dayOfMonthPosition.intValue])
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "DropDown Icon"
                )
            }
            DropdownMenu(
                expanded = isDayOfMonthDropDownExpanded.value,
                onDismissRequest = {
                    isDayOfMonthDropDownExpanded.value = false
                }) {
                daysOfMonthOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(text = option)
                        },
                        onClick = {
                            isDayOfMonthDropDownExpanded.value = false
                            dayOfMonthPosition.intValue = index
                            onDayOfMonthChange(index)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DayOfWeekSelector(
    daysOfWeek: String,
    singleDaySelectable: Boolean = false,
    onDaysOfWeekChange: (String) -> Unit
) {
    var daysOfWeek by remember { mutableStateOf(daysOfWeek) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        days.forEachIndexed { index, day ->
            DayOfWeekIcon(
                enabled = daysOfWeek[index] == '1',
                text = day,
                onClick = { daysOfWeek = toggleDay(daysOfWeek, index, singleDaySelectable, onDaysOfWeekChange) }
            )
        }
    }
}

@Composable
fun DayOfWeekIcon(
    enabled: Boolean = true,
    text: String = "",
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier,
        shape = CircleShape,
        onClick = {
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

fun toggleDay(
    daysOfWeek: String,
    index: Int,
    singleDaySelectable: Boolean,
    onDaysOfWeekChange: (String) -> Unit
): String {
    val updatedDays = daysOfWeek.toMutableList().apply {
        if (singleDaySelectable) {
            // Only allow one day to be selected
            this.forEachIndexed { i, day ->
                if (i != index) {
                    this[i] = '0'
                } else {
                    this[i] = if (this[i] == '1') '0' else '1'
                }
            }
        } else {
            // Toggle the selected day
            this[index] = if (this[index] == '1') '0' else '1'
            if (this.joinToString("") == "0000000") {
                this[index] = '1' // Prevent deselecting all days
            }
        }
    }.joinToString("")

    onDaysOfWeekChange(updatedDays)
    return updatedDays
}