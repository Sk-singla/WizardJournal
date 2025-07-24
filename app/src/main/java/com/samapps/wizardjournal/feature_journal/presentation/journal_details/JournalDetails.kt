package com.samapps.wizardjournal.feature_journal.presentation.journal_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardfeature_presentation.util.customBackground
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JournalDetails(
    modifier: Modifier = Modifier,
    viewModel: JournalDetailsViewModel,
    navController: NavHostController
) {
    val journalState by viewModel.journal.collectAsState()

    when (journalState) {
        is JournalState.Loading -> {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        is JournalState.Success -> {
            JournalDetailsInternal(
                modifier = modifier,
                journal = (journalState as JournalState.Success).journal,
                onEdit = {
                    navController.navigate(Routes.EditJournal((journalState as JournalState.Success).journal.id))
                }
            )
        }

        is JournalState.Error -> {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun JournalDetailsInternal(
    modifier: Modifier = Modifier,
    journal: JournalEntity,
    onEdit: () -> Unit
) {

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEdit()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Journal"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .customBackground(journal.backgroundInfo)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(16.dp)
        ) {

            Text(
                text = journal.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(journal.date),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun JournalDetailsPreview() {
    JournalDetailsInternal(
        journal = JournalEntity(
            title = "Sample Journal",
            content = "This is a sample journal entry.",
            date = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            theme = JournalTheme.None,
            aiPromptUsed = null,
            audioFilePath = null,
            backgroundInfo = BackgroundInfo(
                type = BackgroundType.SOLID_COLOR
            )
        ),
        onEdit = {}
    )
}
