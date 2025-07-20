package com.samapps.wizardjournal.feature_journal.presentation.journal_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.ManualEditorEvent
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.ManualJournalEditorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalEditorViewModel(
    private val journalUseCases: JournalUseCases
): ViewModel() {

    private val _manualEditorState = MutableStateFlow(ManualJournalEditorState())
    val manualEditorState = _manualEditorState.asStateFlow()


    fun onManualEditorEvent(event: ManualEditorEvent) {
        when (event) {
            is ManualEditorEvent.TitleChanged -> {
                _manualEditorState.update {
                    it.copy(title = event.title)
                }
            }

            is ManualEditorEvent.ContentChanged -> {
                _manualEditorState.update {
                    it.copy(content = event.content)
                }
            }

            is ManualEditorEvent.Save -> {
                viewModelScope.launch {
                    journalUseCases.createJournal(
                        JournalEntity(
                            title = _manualEditorState.value.title,
                            content = _manualEditorState.value.content,
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

}