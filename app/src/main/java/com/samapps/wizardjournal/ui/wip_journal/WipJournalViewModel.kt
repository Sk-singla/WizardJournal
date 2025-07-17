package com.samapps.wizardjournal.ui.wip_journal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.samapps.wizardjournal.model.JournalEntry

class WipJournalViewModel(journal: JournalEntry? = null): ViewModel() {
    val isNewJournal = journal == null

    var title by mutableStateOf(journal?.title ?: "")
    var content by mutableStateOf(journal?.content ?: "")
}