package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samapps.wizardjournal.R
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.JournalContentPreview
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.ThemeSelection
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.events.ThemeSelectionEvent

@Composable
fun ThemeSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: JournalEditorViewModel
) {

    val journalContent by viewModel.journalContent.collectAsState()
    val journalTheme by viewModel.journalTheme.collectAsState()

    ThemeSelectionScreenInternal(
        modifier = modifier,
        journalContent = journalContent,
        journalTheme = journalTheme,
        onEvent = { event ->
            viewModel.onThemeSelectionEvents(event)
        },
        onSave = {
            viewModel.onThemeSelectionEvents(ThemeSelectionEvent.GenerateJournal)
        }
    )
}

@Composable
fun ThemeSelectionScreenInternal(
    modifier: Modifier,
    journalContent: String,
    journalTheme: JournalTheme,
    onEvent: (event: ThemeSelectionEvent) -> Unit,
    onSave: () -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            CustomTopAppBar(
                title = "Choose Your Theme"
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(16.dp)
        ) {
            JournalContentPreview(
                previewTitle = "Your Journal Entry",
                content = journalContent,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeSelection(
                value = journalTheme,
                onValueChange = { onEvent(ThemeSelectionEvent.ThemeSelection(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onSave()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.heroicons_sparkles),
                    contentDescription = "Generate Journal!"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generate Journal!")
            }
        }
    }
}

@Preview
@Composable
fun ThemeSelectionScreenPreview() {
    ThemeSelectionScreenInternal(
        modifier = Modifier,
        journalContent = "This is a preview",
        journalTheme = JournalTheme.FantasyAdventure,
        onEvent = {},
        onSave = {}
    )
}
