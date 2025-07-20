package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManualJournalEditorScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalEditorViewModel = koinViewModel()
) {

    val state by viewModel.manualEditorState.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onManualEditorEvent(
                    ManualEditorEvent.Save
                )
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Start Recording"
                )
            }
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TextField(
                value = state.title,
                onValueChange = { viewModel.onManualEditorEvent(ManualEditorEvent.TitleChanged(it)) },
                placeholder = { Text("Title", style = MaterialTheme.typography.headlineSmall) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = state.content,
                onValueChange = { viewModel.onManualEditorEvent(ManualEditorEvent.ContentChanged(it)) },
                placeholder = { Text("Journal", style = MaterialTheme.typography.bodyLarge) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}