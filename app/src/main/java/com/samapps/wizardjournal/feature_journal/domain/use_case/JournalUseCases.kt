package com.samapps.wizardjournal.feature_journal.domain.use_case

data class JournalUseCases(
    val getJournals: GetJournalsUseCase,
    val deleteJournal: DeleteJournalUseCase,
    val createJournal: CreateJournalUseCase,
)
