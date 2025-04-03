package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.util.FirestoreUtil
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor

@Composable
fun NotesPage(
    modifier: Modifier = Modifier,
    tab: String
) {
    // Initialize the RichTextState
    val richTextState = rememberRichTextState()

    // Read the HTML content from Firestore
    FirestoreUtil.readData("notes", tab, { document ->
        richTextState.setHtml(document.getString("data") ?: "")
    }, { exception ->
        Log.e("EditNotes", "Error reading data", exception)
    })

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