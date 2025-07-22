package com.samapps.wizardjournal.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import kotlinx.coroutines.launch

class FeedViewModel(
    private val useCases: JournalUseCases
): ViewModel() {
    val journals = useCases.getJournals()

    fun createNewJournal(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

//        journals.add(JournalEntry(title = title, content = content))
//        println(journals)
        viewModelScope.launch {
            useCases.createJournal(JournalEntity(
                title = title,
                content = content,
                date = System.currentTimeMillis(),
                modifiedTimestamp = System.currentTimeMillis(),
                theme = JournalTheme.None,
                aiPromptUsed = null,
                audioFilePath = null,
                backgroundInfo = BackgroundInfo(
                    type = BackgroundType.SOLID_COLOR,
                    primaryColor = "red",
                    secondaryColor = "blue",
                    patternKey = null,
                    gradientAngle = null
                )
            ))
        }
    }

    fun editJournal(id: Int, title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

//        val index = journals.indexOfFirst { it.id == id }
//        if (index != -1) {
//            journals[index] = journals[index].copy(title = title, content = content)
//        }
        viewModelScope.launch {
            useCases.createJournal(JournalEntity(
                id = id,
                title = title,
                content = content,
                date = System.currentTimeMillis(),
                modifiedTimestamp = System.currentTimeMillis(),
                theme = JournalTheme.None,
                aiPromptUsed = null,
                audioFilePath = null,
                backgroundInfo = BackgroundInfo(
                    type = BackgroundType.SOLID_COLOR,
                    primaryColor = "red",
                    secondaryColor = "blue",
                    patternKey = null,
                    gradientAngle = null
                )
            ))
        }
    }
}