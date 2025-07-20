package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor

sealed class ManualEditorEvent {
    data class TitleChanged(val title: String) : ManualEditorEvent()
    data class ContentChanged(val content: String) : ManualEditorEvent()
    object Save : ManualEditorEvent()
}