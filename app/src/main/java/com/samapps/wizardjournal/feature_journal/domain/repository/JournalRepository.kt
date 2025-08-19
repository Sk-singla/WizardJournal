package com.samapps.wizardjournal.feature_journal.domain.repository

import com.samapps.wizardjournal.feature_journal.domain.model.GenerateJournalRequestDto
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import kotlinx.coroutines.flow.Flow

interface JournalRepository {

    suspend fun syncJournals()
    fun getJournals(): Flow<List<JournalEntity>>

    suspend fun getJournalById(id: Int): JournalEntity?

    suspend fun generateJournal(request: GenerateJournalRequestDto)
    suspend fun insertJournal(journal: JournalEntity)

    suspend fun deleteJournalById(id: Int)
}