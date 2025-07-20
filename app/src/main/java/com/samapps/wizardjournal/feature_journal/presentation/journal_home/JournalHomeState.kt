package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity

data class JournalHomeState(
    val journals: List<JournalEntity> = emptyList(),
    val searchText: String = ""
)