package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme

@Composable
fun ThemeSelection(
    value: JournalTheme,
    onValueChange: (JournalTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeOptions = listOf(
        JournalTheme.HarryPotter,
        JournalTheme.FantasyAdventure,
        JournalTheme.SciFiSaga,
        JournalTheme.NoirMystery,
        JournalTheme.EverydayLife
    )

    // For the custom theme
    val customTheme = value as? JournalTheme.Custom
    var customText by remember(customTheme) { mutableStateOf(customTheme?.name ?: "") }

    Column(modifier = modifier) {

        Text(
            "Choose Your Theme",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        themeOptions.forEach { theme ->
            val selected = value == theme
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onValueChange(theme) },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = if (selected) 4.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                else MaterialTheme.colorScheme.surface,
                border = if (selected)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else null,
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected,
                        onClick = { onValueChange(theme) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(theme.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(theme.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Custom theme option
        Spacer(modifier = Modifier.height(8.dp))

        val isCustomSelected = value is JournalTheme.Custom

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = if (isCustomSelected) 4.dp else 0.dp,
            color = if (isCustomSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            else MaterialTheme.colorScheme.surface,
            border = if (isCustomSelected)
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            else null,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isCustomSelected,
                    onClick = { onValueChange(JournalTheme.Custom(customText)) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Or craft your own theme...", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = customText,
                        onValueChange = {
                            customText = it
                            onValueChange(JournalTheme.Custom(it))
                        },
                        placeholder = { Text("e.g., a cyberpunk detective story") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = isCustomSelected,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ThemeSelectionPreview() {
    ThemeSelection(
        value = JournalTheme.HarryPotter,
        onValueChange = {}
    )
}