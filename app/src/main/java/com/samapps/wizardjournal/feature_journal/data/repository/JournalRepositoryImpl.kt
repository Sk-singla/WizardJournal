package com.samapps.wizardjournal.feature_journal.data.repository

import android.util.Log
import com.samapps.wizardjournal.feature_journal.data.data_source.local.JournalDao
import com.samapps.wizardjournal.feature_journal.data.data_source.remote.JournalApiService
import com.samapps.wizardjournal.feature_journal.domain.model.GenerateJournalRequestDto
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JournalRepositoryImpl(
    private val journalDao: JournalDao,
    private val journalApiService: JournalApiService, // Added JournalApiService
) : JournalRepository {
    override fun getJournals(): Flow<List<JournalEntity>> {
        return journalDao.getJournals()
    }

    override suspend fun syncJournals() {
        try {
            val localJournals = journalDao.getJournals().first() // Assuming getJournals returns Flow
            val prodResponse = journalApiService.fetchAllJournals()

            if (prodResponse.isSuccessful) {
                val prodJournals = prodResponse.body() ?: emptyList()

                val localJournalMap = localJournals.associateBy { it.id }
                val prodJournalMap = prodJournals.associateBy { it.id }

                val journalsToInsertInProd = mutableListOf<JournalEntity>()
                val journalsToInsertInLocal = mutableListOf<JournalEntity>()

                // Compare local journals with prod journals
                localJournals.forEach { localJournal ->
                    val prodJournal = prodJournalMap[localJournal.id]
                    if (prodJournal == null || localJournal.modifiedTimestamp > prodJournal.modifiedTimestamp) {
                        journalsToInsertInProd.add(localJournal)
                    } else if (localJournal.modifiedTimestamp < prodJournal.modifiedTimestamp) {
                        journalsToInsertInLocal.add(prodJournal)
                    }
                }

                prodJournals.forEach { prodJournal ->
                    if (!localJournalMap.containsKey(prodJournal.id)) {
                        journalsToInsertInLocal.add(prodJournal)
                    }
                }

                if(journalsToInsertInLocal.isNotEmpty()){
                    journalDao.insertManyJournals(journalsToInsertInLocal)
                }
                if(journalsToInsertInProd.isNotEmpty()){
                    journalApiService.insertManyJournals(journalsToInsertInProd)
                }
                Log.i("JournalRepository", "Sync successful. Local inserts: ${journalsToInsertInLocal.size}, Prod inserts: ${journalsToInsertInProd.size}")

            } else {
                Log.e("JournalRepository", "API Error fetching journals: ${prodResponse.code()} ${prodResponse.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("JournalRepository", "Network error fetching journals", e)
        }
    }

    override suspend fun getJournalById(id: Int): JournalEntity? {
        return journalDao.getJournalById(id)
    }

    override suspend fun insertJournal(journal: JournalEntity) {
        journalDao.insertJournal(journal) // Insert into local DB first for immediate UI update
        // Perform API call in a background coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = journalApiService.insertJournal(journal)
                if (!response.isSuccessful) {
                    Log.e("JournalRepository", "API Error inserting journal: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("JournalRepository", "Network error inserting journal", e)
            }
        }
    }

    override suspend fun generateJournal(request: GenerateJournalRequestDto) {
        val resp = journalApiService.generateJournal(request)
        if(resp.isSuccessful) {
            resp.body()?.let { generatedJournal ->
                // Insert the generated journal into the local database
                journalDao.insertJournal(generatedJournal)
            } ?: run {
                Log.e("JournalRepository", "Generated journal response body is null")
            }
        } else {
            Log.e("JournalRepository", "API Error: ${resp.code()} ${resp.message()}")
            throw InternalError("Failed to generate journal: ${resp.code()} ${resp.message()}")
        }
    }

    override suspend fun deleteJournalById(id: Int) {
        journalDao.deleteJournalById(id)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = journalApiService.deleteJournalById(id)
                if (!response.isSuccessful) {
                    Log.e("JournalRepository", "API Error deleting journal: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("JournalRepository", "Network error deleting journal", e)
            }
        }
        // Optionally, sync this deletion with the backend API
    }
}