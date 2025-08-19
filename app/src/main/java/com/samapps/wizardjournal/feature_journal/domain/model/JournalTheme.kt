package com.samapps.wizardjournal.feature_journal.domain.model

open class JournalTheme(
    val displayName: String,
    val description: String
) {

    data object None : JournalTheme(
        displayName = "None",
        description = "No theme"
    )
    data object HarryPotter : JournalTheme(
        displayName = "Harry Potter",
        description = "Magical wizarding world adventure"
    )
    data object FantasyAdventure : JournalTheme(
        displayName = "Fantasy Adventure",
        description = "Epic quests and mythical creatures"
    )
    data object SciFiSaga : JournalTheme(
        displayName = "Sci-Fi Saga",
        description = "Futuristic space adventures"
    )
    data object NoirMystery : JournalTheme(
        displayName = "Noir Mystery",
        description = "Dark and eerie suspense"
    )
    data object EverydayLife : JournalTheme(
        displayName = "Everyday Life",
        description = "Real-world personal stories"
    )
    data class Custom(val name: String) : JournalTheme(
        displayName = "Custom",
        description = "Custom theme"
    )

    // Helper to convert to a DB string representation
    fun toDBString(): String = when (this) {
        None -> "None"
        HarryPotter -> "HarryPotter"
        FantasyAdventure -> "FantasyAdventure"
        SciFiSaga -> "SciFiSaga"
        NoirMystery -> "NoirMystery"
        EverydayLife -> "EverydayLife"
        else -> "Custom:${this.displayName}"
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
