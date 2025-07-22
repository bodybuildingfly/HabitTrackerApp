package com.example.habittrackerapp.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.model.view.AppViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor

@Composable
fun NotesPage(
    appViewModel: AppViewModel
) {
    // Initialize the RichTextState
    val richTextState = rememberRichTextState()
    val tab = appViewModel.activeNotesTab.value

    // Fetch the string content from the ViewModel
    LaunchedEffect(Unit) {
        when (tab) {
            "rules" -> richTextState.setHtml(appViewModel.rules.value.toString())
            "limits" -> richTextState.setHtml(appViewModel.limits.value.toString())
            "ideas" -> richTextState.setHtml(appViewModel.ideas.value.toString())
            "notes" -> richTextState.setHtml(appViewModel.notes.value.toString())
            else -> richTextState.setHtml("")
        }
    }

    BasicRichTextEditor(
        readOnly = true,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        state = richTextState,
        textStyle = LocalTextStyle.current.copy(
            fontFamily = FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onBackground
        )
    )
}