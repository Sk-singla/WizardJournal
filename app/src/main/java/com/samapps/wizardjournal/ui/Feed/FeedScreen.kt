package com.samapps.wizardjournal.ui.Feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.CreateNewJournalScreen
import com.samapps.wizardjournal.EditJournalScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavHostController, viewModel: FeedViewModel, modifier: Modifier = Modifier) {
    fun handleCreateNewJournal() {
        navController.navigate(CreateNewJournalScreen)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("My Journals")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (viewModel.journals.isNotEmpty()){
                IconButton(
                    onClick = {
                        handleCreateNewJournal()
                    },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }
    ) { values ->

        if (viewModel.journals.isEmpty()){
            Column(
                modifier = Modifier.fillMaxSize().padding(values),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No journal found! Create new Journal"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        handleCreateNewJournal()
                    }
                ) {
                    Text("Create New Journal")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(values).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
            ) {
                items(viewModel.journals){ journal ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(
                                    EditJournalScreen(journal.id)
                                )
                            }
                    ) {
                        Text(
                            text = journal.title,
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Add spacing within item
                        Text(
                            text = journal.lastModified.toString()
                        )
                    }
                }
            }
        }

    }
}