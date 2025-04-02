package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.components.ExecuteOnKeyboardOpen
import com.example.habittrackerapp.model.data.Message
import com.example.habittrackerapp.util.FirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onClose: () -> Unit
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isLoading by remember { mutableStateOf(true) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Fetch messages from Firebase
    LaunchedEffect(Unit) {
        Log.d("ChatScreen", "Fetching messages...")
        FirebaseUtil.getMessages { messagesList ->
            messages = messagesList

            coroutineScope.launch {
                // Check if messages are available and not scrolled to the end
                if (messages.isNotEmpty()) {
                    listState.requestScrollToItem(messages.size)
                }
                isLoading = false
            }
        }
    }

    // Scroll to the bottom when the keyboard opens
    ExecuteOnKeyboardOpen {
        coroutineScope.launch {
            listState.requestScrollToItem(messages.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Chat"
                    )
                },
                navigationIcon = {

                    // Back button
                    IconButton(onClick = { onClose() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                // Settings icon button on the right side
                actions = {
                    IconButton(onClick = { /* Do something... */ }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding() // Keeps the bottom of the column above the navigation bar
        ) {
            if (isLoading) {
                // Show loading indicator until scrolling is completed
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    items(messages.size) { index ->
                        MessageItem(
                            message = messages[index],
                            isCurrentUser = messages[index].senderId == currentUser?.uid
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = CircleShape
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (messageText.text.isNotBlank()) {
                                val messageData = Message(
                                    messageText.text,
                                    currentUser?.uid ?: "",
                                    System.currentTimeMillis().toString()
                                )
                                FirebaseUtil.addMessage(messageData, onComplete = {})
                                messageText = TextFieldValue("")
                            }
                        }
                    ) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.Gray

    // Custom rounded shape: Straight on the side closest to the sender
    val messageShape = if (isCurrentUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) // Right-bottom straight
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp) // Left-bottom straight
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = messageShape,
            shadowElevation = 1.dp,
            color = backgroundColor
        ) {
            Text(
                text = message.message,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
        Text(
            modifier = Modifier.align(alignment),
            text = convertTimestamp(message.timestamp)
                .toString(),
            fontSize = 12.sp
        )
    }
}

fun convertTimestamp(timestamp: String): String {
    val timestampMillis = timestamp.toLongOrNull()?.let {
        if (timestamp.length == 10) it * 1000 else it
    } ?: return ""

    val date = Date(timestampMillis)
    val calendar = Calendar.getInstance()
    calendar.time = date
    val currentTime = Calendar.getInstance()

    return when {
        // Today
        calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR) -> {
            "Today " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
        // Yesterday
        calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR) - 1 -> {
            "Yesterday " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
        // Older messages
        else -> {
            SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(date)
        }
    }
}