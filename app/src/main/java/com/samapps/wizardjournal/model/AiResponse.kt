package com.samapps.wizardjournal.model

import kotlinx.serialization.Serializable

@Serializable
data class AiResponse(
    val transcription: String,
    val journalTitle: String,
    val journalStory: String
)
