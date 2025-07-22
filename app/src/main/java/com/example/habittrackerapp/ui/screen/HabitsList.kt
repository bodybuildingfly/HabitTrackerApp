package com.example.habittrackerapp.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.habittrackerapp.util.Util.convertTimestamp

@Composable
fun HabitsList(
    modifier: Modifier = Modifier,
    habitViewModel: HabitViewModel,
    onHabitClick: (String) -> Unit,
    onHabitNew: () -> Unit
) {
    // Get the list of habits from the view model
    val habits by habitViewModel.habits.observeAsState(emptyList())
    val dueDates = habits.groupBy { it.nextDueDate }
    val sortedDueDates = dueDates.toSortedMap()
    val dueDatesList = sortedDueDates.map {
        DueDateCategory(
            due = it.key,
            items = it.value
        )
    }

    LaunchedEffect(Unit) {
        habitViewModel.fetchHabits()
    }

    CategorizedLazyList(
        modifier = modifier,
        dueDates = dueDatesList,
        onHabitClick = onHabitClick
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick = {
                onHabitNew()
            },
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, "Add habit")
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onHabitClick(habit.id) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
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

data class DueDateCategory(
    val due: Long,
    val items: List<Habit>
)

@Composable
private fun DueDateHeader(
    modifier: Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun CategorizedLazyList (
    modifier: Modifier = Modifier,
    dueDates: List<DueDateCategory>,
    onHabitClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        dueDates.forEach { dueDate ->
            stickyHeader {
                DueDateHeader(
                    modifier = modifier,
                    text = formatTimeStamp(dueDate.due)
                )
            }
            items(
                count = dueDate.items.size,
                key = { index -> dueDate.items[index].id }
            ) {
                Habit(
                    habit = dueDate.items[it],
                    onHabitClick = onHabitClick
                )
            }
        }
    }
}

// Remove the time from the timestamp
fun formatTimeStamp(
    timestamp: Long
): String {
    val dateTime = convertTimestamp(timestamp).toString()
    return if (dateTime.indexOf(",") != -1)
        dateTime.substring(0, dateTime.indexOf(","))
    else
        dateTime.substring(0, dateTime.indexOf(" "))
}