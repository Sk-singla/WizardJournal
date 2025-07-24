package com.samapps.wizardjournal.feature_journal.presentation.journal_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class JournalState {
    data object Loading : JournalState()
    data class Success(val journal: JournalEntity) : JournalState()
    data object Error : JournalState()
}

class JournalDetailsViewModel(
    private val journalUseCases: JournalUseCases,
    private val journalId: Int
): ViewModel() {
    private val _journal = MutableStateFlow<JournalState>(JournalState.Loading)
    val journal = _journal.asStateFlow()

    init {
        viewModelScope.launch {
            val result = journalUseCases.getJournalById(journalId)
            _journal.value = if (result != null) {
                JournalState.Success(result)
            } else {
                JournalState.Error
            }
        }
    }
}