package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.model.view.AppViewModel

@Composable
fun JournalList(
    appViewModel: AppViewModel
) {
    // Remember state for the user
    val appUIState = appViewModel.uiState.collectAsState()

    Log.d("JournalList", "User data: ${appUIState.value.user}")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Journal Screen", fontSize = 24.sp)
    }
}