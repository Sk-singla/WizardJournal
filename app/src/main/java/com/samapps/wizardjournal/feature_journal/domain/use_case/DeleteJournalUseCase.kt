package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository

class DeleteJournalUseCase(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteJournalById(id)
    }
}