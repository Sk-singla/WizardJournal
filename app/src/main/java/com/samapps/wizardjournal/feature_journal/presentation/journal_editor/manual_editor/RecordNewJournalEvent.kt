package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor

sealed class RecordNewJournalEvent {
    data class CaptureText(val content: String) : RecordNewJournalEvent()
}