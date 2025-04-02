package com.example.habittrackerapp.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(title)
        },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null
            )
        },
        onClick = { onClick() }
    )
}