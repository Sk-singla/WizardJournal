package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.components.JournalCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun JournalHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalHomeViewModel = koinViewModel()
) {

    val state = viewModel.state.collectAsState()
    fun handleCreateNewJournal() {
        navController.navigate(Routes.CreateNewJournalByRecording)
    }

    fun handleEditJournal() {
        println("Journal card clicked")
    }

    if (state.value.journals.isEmpty()) {
        EmptyJournalScreen(
            modifier = modifier,
            onCreateNewJournal = { handleCreateNewJournal() }
        )
    } else {
        JournalsListScreen(
            modifier = modifier,
            journals = state.value.journals,
            onJournalClick = { handleEditJournal() },
            onCreateNewJournal = { handleCreateNewJournal() }
        )
    }
}

@Composable
private fun EmptyJournalScreen(modifier: Modifier = Modifier, onCreateNewJournal: () -> Unit) {

    Scaffold(
        modifier = modifier,
        topBar = {
            CustomTopAppBar(
                title = "Wizournal"
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Journals Found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreateNewJournal) {
                Text(text = "Create New Journal")
            }
        }
    }
}

@Composable
private fun JournalsListScreen(
    modifier: Modifier = Modifier,
    journals: List<JournalEntity>,
    onJournalClick: (JournalEntity) -> Unit,
    onCreateNewJournal: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CustomTopAppBar(
                title = "Wizournal"
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onCreateNewJournal() }) {
                Icon(Icons.Filled.Add, "Add new journal")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(journals) { journal ->
                JournalCard(
                    journal = journal,
                    onClick = { onJournalClick(journal) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}