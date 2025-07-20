package com.samapps.wizardjournal.feature_journal.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class JournalEntity(
    val title: String,
    val content: String,
    val date: Long,
    val modifiedTimestamp: Long,
    val theme: JournalTheme,
    val aiPromptUsed: String?,
    val audioFilePath: String?,
    val backgroundInfo: BackgroundInfo,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)


class InvalidJournalException(message: String): Exception(message)