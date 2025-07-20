package com.samapps.wizardjournal.feature_journal.domain.model_converters

import androidx.room.TypeConverter
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import kotlinx.serialization.json.Json

class JournalConverters {
    @TypeConverter
    fun fromJournalTheme(theme: JournalTheme): String {
        return theme.toDBString()
    }

    @TypeConverter
    fun toJournalTheme(value: String): JournalTheme {
        return JournalTheme.fromDBString(value)
    }

    @TypeConverter
    fun fromBackgroundInfo(backgroundInfo: BackgroundInfo): String {
        return Json.encodeToString(backgroundInfo)
    }

    @TypeConverter
    fun toBackgroundInfo(json: String): BackgroundInfo {
        return Json.decodeFromString<BackgroundInfo>(json)
    }
}