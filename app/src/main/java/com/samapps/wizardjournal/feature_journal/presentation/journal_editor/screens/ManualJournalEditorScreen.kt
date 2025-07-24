package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.TransparentTextField
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events.ManualEditorEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManualJournalEditorScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalEditorViewModel = koinViewModel()
) {

    val journalTitle by viewModel.journalTitle.collectAsState()
    val journalContent by viewModel.journalContent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.manualEditorEvents.collectLatest { event ->
            when (event) {
                is JournalEditorViewModel.ManualJournalEditorScreenUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is JournalEditorViewModel.ManualJournalEditorScreenUiEvent.JournalSaved -> {
                    navController.popBackStack(
                        route = Routes.JournalHome::class,
                        inclusive = false
                    )
                }
            }
        }
    }


    ManualJournalEditorScreenInternal(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        isEditing = viewModel.isEditing,
        journalTitle = journalTitle,
        journalContent = journalContent,
        onEvent = { event ->
            viewModel.onManualEditorEvent(event)
        },
        onSave = {
            viewModel.onManualEditorEvent(
                ManualEditorEvent.Save
            )
        }
    )
}

@Composable
private fun ManualJournalEditorScreenInternal(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    isEditing: Boolean,
    journalTitle: String,
    journalContent: String,
    onEvent: (ManualEditorEvent) -> Unit,
    onSave: () -> Unit,
) {

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomTopAppBar(
                title = if (isEditing) "Edit Journal" else "Create Journal",
                actions = {
                    IconButton(
                        onClick = { onSave() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TransparentTextField(
                value = journalTitle,
                onValueChange = { onEvent(ManualEditorEvent.TitleChanged(it)) },
                placeholder = "Title",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            TransparentTextField(
                value = journalContent,
                onValueChange = { onEvent(ManualEditorEvent.ContentChanged(it)) },
                placeholder = "Journal",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun ManualJournalEditorScreenPreview() {
    ManualJournalEditorScreenInternal(
        isEditing = false,
        journalTitle = "Title",
        journalContent = "Content",
        onEvent = {},
        onSave = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}