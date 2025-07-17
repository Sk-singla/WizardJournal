package com.samapps.wizardjournal.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)
