package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.events

import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme

sealed class ThemeSelectionEvent{
    data class ThemeSelection(val theme: JournalTheme): ThemeSelectionEvent()
    data object GenerateJournal: ThemeSelectionEvent()
}
