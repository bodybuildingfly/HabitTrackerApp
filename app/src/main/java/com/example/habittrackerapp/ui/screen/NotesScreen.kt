package com.example.habittrackerapp.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.model.view.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    appViewModel: AppViewModel,
    onEdit: (String) -> Unit
) {
    // Create the top row of buttons
    var pagesList = listOf<String>("RULES", "LIMITS", "IDEAS", "NOTES")

    // Create the pager state
    val pagerState = rememberPagerState(initialPage = 0) { pagesList.size }

    // Track the selected tab
    var selectedTab by remember { mutableIntStateOf(pagerState.currentPage) }

    // Scroll state for the screen
    val scrollState = rememberScrollState(0)

    // Listen for changes in the selected tab
    LaunchedEffect(selectedTab) {
        pagerState.scrollToPage(selectedTab)
    }

    Scaffold(
        topBar = {
            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                selectedTabIndex = selectedTab,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                for (index in pagesList.indices) {
                    Tab(
                        selected = index == selectedTab,
                        onClick = {
                            selectedTab = index
                            appViewModel.setActiveTab(pagesList[index].lowercase())
                        }
                    ) {
                        Text(
                            text = pagesList[index],
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    // Pass the tab argument to the NotesEdit composable
                    onEdit(pagesList[selectedTab].lowercase())
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Edit, "Edit notes")
            }
        },
    ) { paddingValue ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
                .padding(paddingValue)
                .verticalScroll(scrollState),
            color = MaterialTheme.colorScheme.background
        ) {
            HorizontalPager(
                pageSize = PageSize.Fill,
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { currentPage ->
                NotesPage(
                    appViewModel = appViewModel
                )
            }
        }
    }
}