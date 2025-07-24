package com.samapps.wizardjournal.app

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Root: Routes

    @Serializable
    data object JournalHome: Routes

    @Serializable
    data object CreateNewJournalByRecording: Routes

    @Serializable
    data object CreateNewJournalManualEditing: Routes

    @Serializable
    data object CreateNewJournalThemeSelection: Routes

    @Serializable
    data class EditJournal(val journalId: Int): Routes
}