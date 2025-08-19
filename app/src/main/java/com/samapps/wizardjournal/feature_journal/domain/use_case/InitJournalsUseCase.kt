package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository

class InitJournalsUseCase(
    private val repository: JournalRepository
) {
    suspend operator fun invoke() {
        repository.syncJournals()
    }
}
