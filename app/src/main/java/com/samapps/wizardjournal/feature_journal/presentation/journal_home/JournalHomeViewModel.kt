package com.samapps.wizardjournal.feature_journal.presentation.journal_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalHomeViewModel(private val journalUseCases: JournalUseCases): ViewModel() {
    private val _state = MutableStateFlow(JournalHomeState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getJournalsJob: Job? = null

    init {
        // Initialize journals from the API first
        viewModelScope.launch {
            try {
                journalUseCases.initJournals()
                // Optionally, you can emit an event or log success
            } catch (e: Exception) {
                // Handle error, e.g., show a snackbar or log
                _eventFlow.emit(UiEvent.ShowSnackbar("Error initializing journals: ${e.localizedMessage}"))
            }
        }
        // Then, start observing local journals
        getJournals()
    }

    fun onEvent(event: JournalHomeEvent) {
        when(event) {
            is JournalHomeEvent.SearchJournal -> {
                _state.update {
                    it.copy(searchText = event.searchText)
                }
            }
            is JournalHomeEvent.DeleteJournal -> {
                viewModelScope.launch {
                    journalUseCases.deleteJournal(event.journalId)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Journal Deleted!"))
                }
            }
        }
    }

    private fun getJournals() {
        getJournalsJob?.cancel()
        getJournalsJob = journalUseCases.getJournals()
            .onEach {
                _state.value = _state.value.copy(journals = it)
            }
            .launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
    }
}
