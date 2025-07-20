package com.samapps.wizardjournal.feature_journal.data.repository

import com.samapps.wizardjournal.feature_journal.data.data_source.JournalDao
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow

class JournalRepositoryImpl(private val journalDao: JournalDao): JournalRepository {
    override fun getJournals(): Flow<List<JournalEntity>> {
        return journalDao.getJournals()
    }

    override suspend fun getJournalById(id: Int): JournalEntity? {
        return journalDao.getJournalById(id)
    }

    override suspend fun insertJournal(journal: JournalEntity) {
        journalDao.insertJournal(journal)
    }

    override suspend fun deleteJournalById(id: Int) {
        journalDao.deleteJournalById(id)
    }
}