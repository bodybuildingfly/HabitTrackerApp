package com.example.habittrackerapp.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.model.data.Habit
import com.example.habittrackerapp.model.view.HabitViewModel

@Composable
fun HabitsList(
    modifier: Modifier = Modifier,
    habitViewModel: HabitViewModel,
    onHabitClick: (String) -> Unit
) {
    // Get the list of habits from the view model
    val habits by habitViewModel.habits.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        habitViewModel.fetchHabits()
    }

    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(habits) { habit ->
            Habit(
                habit = habit,
                onHabitClick = {
                    onHabitClick(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Habit(
    habit: Habit,
    modifier: Modifier = Modifier,
    onHabitClick: (String) -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onHabitClick(habit.id) },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = modifier
                    .weight(.80F)
            ) {
                Text(
                    modifier = modifier,
                    text = habit.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = habit.description,
                    modifier = modifier,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = modifier
                    .weight(.20F),
                text = "${habit.timesCompleted}/${habit.timesToComplete}",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}