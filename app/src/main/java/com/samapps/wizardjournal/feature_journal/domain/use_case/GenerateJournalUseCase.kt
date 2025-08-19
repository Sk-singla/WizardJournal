package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.model.GenerateJournalRequestDto
import com.samapps.wizardjournal.feature_journal.domain.model.InvalidJournalException
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository

class GenerateJournalUseCase(
    private val repository: JournalRepository
) {

    @Throws(InvalidJournalException::class)
    suspend operator fun invoke(userInput: String, theme: JournalTheme) {
        if (userInput.isBlank()) {
            throw InvalidJournalException("User input cannot be empty.")
        }

        repository.generateJournal(GenerateJournalRequestDto(
            userInput = userInput,
            theme = theme
        ))
    }
}