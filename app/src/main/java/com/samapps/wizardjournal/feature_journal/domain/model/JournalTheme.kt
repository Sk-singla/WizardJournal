package com.samapps.wizardjournal.feature_journal.domain.model

sealed class JournalTheme {
    data object None : JournalTheme()
    data object HarryPotter : JournalTheme()
    data object FantasyAdventure : JournalTheme()
    data object SciFiSaga : JournalTheme()
    data object NoirMystery : JournalTheme()
    data object EverydayLife : JournalTheme()
    data class Custom(val name: String) : JournalTheme()

    // Helper to convert to a DB string representation
    fun toDBString(): String = when (this) {
        None -> "None"
        HarryPotter -> "HarryPotter"
        FantasyAdventure -> "FantasyAdventure"
        SciFiSaga -> "SciFiSaga"
        NoirMystery -> "NoirMystery"
        EverydayLife -> "EverydayLife"
        is Custom -> "Custom:${this.name}"
    }

    companion object {
        // Helper to convert from a DB string representation
        fun fromDBString(value: String): JournalTheme {
            return when {
                value == "None" -> None
                value == "HarryPotter" -> HarryPotter
                value == "FantasyAdventure" -> FantasyAdventure
                value == "SciFiSaga" -> SciFiSaga
                value == "NoirMystery" -> NoirMystery
                value == "EverydayLife" -> EverydayLife
                value.startsWith("Custom:") -> Custom(value.substringAfter("Custom:"))
                else -> EverydayLife // Default or error handling
            }
        }
    }
}
