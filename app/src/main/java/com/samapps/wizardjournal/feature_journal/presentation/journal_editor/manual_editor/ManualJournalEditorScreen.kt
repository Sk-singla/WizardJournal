package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.TransparentTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManualJournalEditorScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalEditorViewModel = koinViewModel()
) {

    val journalTitle by viewModel.journalTitle.collectAsState()
    val journalContent by viewModel.journalContent.collectAsState()

    ManualJournalEditorScreenInternal(
        modifier = modifier,
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
            navController.popBackStack(
                route = Routes.JournalHome::class,
                inclusive = false
            )
        }
    )
}

@Composable
private fun ManualJournalEditorScreenInternal(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    journalTitle: String,
    journalContent: String,
    onEvent: (ManualEditorEvent) -> Unit,
    onSave: () -> Unit,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            CustomTopAppBar(
                title = if (isEditing) "Edit Journal" else "Create Journal",
                actions = {
                    IconButton(
                        onClick = { onSave() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save"
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
        onSave = {}
    )
}