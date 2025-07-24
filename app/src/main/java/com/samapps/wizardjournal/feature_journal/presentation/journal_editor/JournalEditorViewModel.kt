package com.samapps.wizardjournal.feature_journal.presentation.journal_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.ManualEditorEvent
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.RecordNewJournalEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalEditorViewModel(
    private val journalUseCases: JournalUseCases,
): ViewModel() {

    private val _journalTitle = MutableStateFlow("")
    val journalTitle = _journalTitle.asStateFlow()

    private val _journalContent = MutableStateFlow("")
    val journalContent = _journalContent.asStateFlow()

    private var journalId: Int? = null
    val isEditing: Boolean
        get() = journalId != null

    fun setJournalIdToEdit(id: Int?) {
        journalId = id
        if (id == null) {
            return
        }

        viewModelScope.launch {
            val journal = journalUseCases.getJournalById(id)
            _journalTitle.update {
                journal?.title ?: ""
            }
            _journalContent.update {
                journal?.content ?: ""
            }
        }
    }


    fun onManualEditorEvent(event: ManualEditorEvent) {
        when (event) {
            is ManualEditorEvent.TitleChanged -> {
                _journalTitle.update {
                    event.title
                }
            }

            is ManualEditorEvent.ContentChanged -> {
                _journalContent.update {
                    event.content
                }
            }

            is ManualEditorEvent.Save -> {
                viewModelScope.launch {
                    journalUseCases.createJournal(
                        JournalEntity(
                            title = _journalTitle.value,
                            content = _journalContent.value,
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
                        )
                    )
                }
            }
        }
    }

    fun onRecordNewJournalEvent(event: RecordNewJournalEvent) {
        when (event) {
            is RecordNewJournalEvent.CaptureText -> {
                _journalContent.update {
                    if (it.isEmpty()) {
                        event.content
                    } else {
                        it + " " + event.content
                    }
                }
            }
        }
    }
}