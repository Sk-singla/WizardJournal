package com.samapps.wizardjournal.feature_journal.domain.repository

import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import kotlinx.coroutines.flow.Flow

interface JournalRepository {

    fun getJournals(): Flow<List<JournalEntity>>

    suspend fun getJournalById(id: Int): JournalEntity?

    suspend fun insertJournal(journal: JournalEntity)

    suspend fun deleteJournalById(id: Int)
}