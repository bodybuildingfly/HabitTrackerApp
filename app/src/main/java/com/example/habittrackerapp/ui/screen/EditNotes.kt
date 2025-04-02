package com.example.habittrackerapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.components.RichTextStyleRow
import com.example.habittrackerapp.util.FirebaseUtil
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNotes(
    tab: String,
    onClose: () -> Unit
) {

    val outlinedRichTextState = rememberRichTextState()
    val tabTitle = tab.replaceFirstChar { it.uppercase() }

    FirebaseUtil.readData("notes/${tab}", {
        outlinedRichTextState.setHtml(it.child("data").value.toString())
    }, {
        Log.e("EditNotes", "Error reading data", it.toException())
    })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    // Back button
                    IconButton(onClick = { onClose() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = "Edit $tabTitle"
                    )
                },
                actions = {
                    IconButton(onClick = {
                        FirebaseUtil.writeDataWithTransaction("notes/${tab}", outlinedRichTextState.toHtml(), {
                            onClose()
                        }, {
                            Log.e("EditNotes", "Error writing data", it)
                        })
                    }) {
                        Icon(
                            Icons.Outlined.Save,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValue)
                .windowInsetsPadding(WindowInsets.ime)
                .fillMaxSize()
        ) {

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                RichTextStyleRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = outlinedRichTextState
                )
            }

            item {
                OutlinedRichTextEditor(
                    readOnly = false,
                    modifier = Modifier.fillMaxSize(),
                    state = outlinedRichTextState
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            }
        }
    }
}