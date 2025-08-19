package com.samapps.wizardjournal.feature_journal.domain.model

data class GenerateJournalRequestDto(
    val userInput: String,
    val theme: JournalTheme
)
