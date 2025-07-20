package com.samapps.wizardjournal.feature_journal.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model_converters.JournalConverters

@Database(
    entities = [JournalEntity::class],
    version = 1,
)
@TypeConverters(JournalConverters::class)
abstract class JournalDatabase: RoomDatabase() {

    abstract val journalDao: JournalDao

    companion object {
        const val DATABASE_NAME = "journal_db"
    }

}