package com.samapps.wizardjournal.feature_journal.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journals")
    fun getJournals(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journals WHERE id = :id")
    suspend fun getJournalById(id: Int): JournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Query("DELETE FROM journals WHERE id = :id")
    suspend fun deleteJournalById(id: Int)
}