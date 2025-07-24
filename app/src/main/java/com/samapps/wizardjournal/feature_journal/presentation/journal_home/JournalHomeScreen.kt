package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.components.JournalCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    fun handleEditJournal(journalId: Int) {
        navController.navigate(Routes.ViewJournal(journalId))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is JournalHomeViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    if (state.value.journals.isEmpty()) {
        EmptyJournalScreen(
            modifier = modifier,
            onCreateNewJournal = { handleCreateNewJournal() }
        )
    } else {
        JournalsListScreen(
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            journals = state.value.journals,
            onJournalClick = { handleEditJournal(it.id) },
            onCreateNewJournal = { handleCreateNewJournal() },
            onDeleteJournal = { viewModel.onEvent(JournalHomeEvent.DeleteJournal(it)) }
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
    snackbarHostState: SnackbarHostState,
    journals: List<JournalEntity>,
    onJournalClick: (JournalEntity) -> Unit,
    onCreateNewJournal: () -> Unit,
    onDeleteJournal: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(0.dp, 8.dp)
        ) {
            items(journals, key = {it.id}) { journal ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                            scope.launch {
                                onDeleteJournal(journal.id)
                            }
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Journal",
                                modifier = Modifier.padding(16.dp, 0.dp, 0.dp)
                            )
                        }
                    },
                    modifier = Modifier.animateItem()
                ) {
                    JournalCard(
                        journal = journal,
                        onClick = { onJournalClick(journal) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}