package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.model.InvalidJournalException
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository

class CreateJournalUseCase(
    private val repository: JournalRepository
) {

    @Throws(InvalidJournalException::class)
    suspend operator fun invoke(journal: JournalEntity) {
        if(journal.title.isBlank()) {
            throw InvalidJournalException("The title of the journal can't be empty.")
        }
        if(journal.content.isBlank()) {
            throw InvalidJournalException("The content of the journal can't be empty.")
        }
        if(journal.date == 0L) {
            throw InvalidJournalException("The date of the journal can't be empty.")
        }
        if(journal.modifiedTimestamp == 0L) {
            throw InvalidJournalException("The modified timestamp of the journal can't be empty.")
        }

        repository.insertJournal(journal)
    }
}