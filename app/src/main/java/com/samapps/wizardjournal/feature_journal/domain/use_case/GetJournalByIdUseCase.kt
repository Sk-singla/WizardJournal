package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository

class GetJournalByIdUseCase(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(id: Int): JournalEntity? {
        return repository.getJournalById(id)
    }
}