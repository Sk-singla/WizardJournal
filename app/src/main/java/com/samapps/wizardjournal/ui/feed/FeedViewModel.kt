package com.samapps.wizardjournal.ui.feed

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.samapps.wizardjournal.model.JournalEntry

class FeedViewModel: ViewModel() {
    val journals = mutableStateListOf<JournalEntry>(
        JournalEntry(
            title = "My First Journal",
            content = "This is my first journal entry."
        )

    )

    fun createNewJournal(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

        journals.add(JournalEntry(title = title, content = content))
        println(journals)
    }

    fun editJournal(id: String, title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

        val index = journals.indexOfFirst { it.id == id }
        if (index != -1) {
            journals[index] = journals[index].copy(title = title, content = content)
        }
    }
}