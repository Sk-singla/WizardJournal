package com.samapps.wizardjournal.feature_journal.presentation.journal_home

sealed class JournalHomeEvent {
    data class SearchJournal(val searchText: String): JournalHomeEvent()
    data class DeleteJournal(val journalId: Int): JournalHomeEvent()
}