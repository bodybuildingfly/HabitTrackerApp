package com.example.habittrackerapp.model.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeScreen(
    val icon: ImageVector,
    val route: String,
    val screen: @Composable (((String) -> Unit) -> Unit),
    val badgeAmount: Int?
)
