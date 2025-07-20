package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class JournalHomeViewModel(private val journalUseCases: JournalUseCases): ViewModel() {
    private val _state = MutableStateFlow(JournalHomeState())
    val state = _state.asStateFlow()

    private var getJournalsJob: Job? = null

    init {
        getJournals()
    }

    fun onEvent(event: JournalHomeEvent) {
        when(event) {
            is JournalHomeEvent.SearchJournal -> {
                _state.update {
                    it.copy(searchText = event.searchText)
                }
            }
        }
    }

    fun getJournals() {
        getJournalsJob?.cancel()
        getJournalsJob = journalUseCases.getJournals()
            .onEach {
                _state.value = _state.value.copy(journals = it)
            }
            .launchIn(viewModelScope)
    }
}