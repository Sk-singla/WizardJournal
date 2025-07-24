package com.samapps.wizardjournal.feature_journal.presentation.journal_home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.samapps.wizardfeature_presentation.util.customBackground
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalCard(modifier: Modifier = Modifier, journal: JournalEntity, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(journal.date))

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier
            .customBackground(journal.backgroundInfo)
            .padding(16.dp)
        ) {
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
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}