package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.CreateNewJournalScreen
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalHomeViewModel = koinViewModel()
) {

    val state = viewModel.state.collectAsState()
    fun handleCreateNewJournal () {
        println("Create new journal button clicked")
        navController.navigate(CreateNewJournalScreen)

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
fun EmptyJournalScreen(modifier: Modifier = Modifier, onCreateNewJournal: () -> Unit) {

    Scaffold { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            Text(text = "No Journals Found")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreateNewJournal) {
                Text(text = "Create New Journal")
            }
        }
    }
}

@Composable
fun JournalsListScreen(
    modifier: Modifier = Modifier,
    journals: List<JournalEntity>,
    onJournalClick: (JournalEntity) -> Unit,
    onCreateNewJournal: () -> Unit
) {
    Scaffold(
        modifier = modifier,
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

@Composable
fun JournalCard(modifier: Modifier = Modifier, journal: JournalEntity, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(journal.date))

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = journal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = dateString, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)
        }
    }
}