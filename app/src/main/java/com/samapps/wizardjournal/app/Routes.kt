package com.samapps.wizardjournal.app

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object JournalHome: Routes

    @Serializable
    data object CreateNewJournal: Routes

    @Serializable
    data class EditJournal(val journalId: Int): Routes
}