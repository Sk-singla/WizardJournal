package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events

sealed class ManualEditorEvent {
    data class TitleChanged(val title: String) : ManualEditorEvent()
    data class ContentChanged(val content: String) : ManualEditorEvent()
    data object Save : ManualEditorEvent()
}