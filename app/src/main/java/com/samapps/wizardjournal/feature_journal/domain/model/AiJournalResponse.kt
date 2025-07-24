package com.samapps.wizardjournal.feature_journal.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiJournalResponse(
    val title: String,
    val content: String,
    val backgroundInfo: BackgroundInfo
)