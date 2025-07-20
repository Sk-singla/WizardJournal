package com.samapps.wizardjournal.feature_journal.domain.use_case

import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository
import com.samapps.wizardjournal.feature_journal.domain.util.JournalOrder
import com.samapps.wizardjournal.feature_journal.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetJournalsUseCase(
    private val repository: JournalRepository
) {

    operator fun invoke(
        journalOrder: JournalOrder = JournalOrder.Date(OrderType.Descending)
    ): Flow<List<JournalEntity>> {
        return repository.getJournals().map {
            when(journalOrder.orderType) {
                is OrderType.Ascending -> {
                    when(journalOrder) {
                        is JournalOrder.Date -> it.sortedBy { it.date }
                        is JournalOrder.ModifiedTimestamp -> it.sortedBy { it.modifiedTimestamp }
                        is JournalOrder.Title -> it.sortedBy { it.title.lowercase() }
                        is JournalOrder.Content -> it.sortedBy { it.content.lowercase() }
                    }
                }
                is OrderType.Descending -> {
                    when(journalOrder) {
                        is JournalOrder.Date -> it.sortedByDescending { it.date }
                        is JournalOrder.ModifiedTimestamp -> it.sortedByDescending { it.modifiedTimestamp }
                        is JournalOrder.Title -> it.sortedByDescending { it.title.lowercase() }
                        is JournalOrder.Content -> it.sortedByDescending { it.content.lowercase() }
                    }
                }
            }
        }
    }
}