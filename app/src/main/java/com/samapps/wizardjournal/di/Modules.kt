package com.samapps.wizardjournal.di

import androidx.room.Room
import com.samapps.wizardjournal.feature_journal.data.data_source.JournalDatabase
import com.samapps.wizardjournal.feature_journal.data.repository.JournalRepositoryImpl
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository
import com.samapps.wizardjournal.feature_journal.domain.use_case.CreateJournalUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.DeleteJournalUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.GetJournalByIdUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.GetJournalsUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import com.samapps.wizardjournal.feature_journal.presentation.journal_details.JournalDetailsViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.JournalHomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            JournalDatabase::class.java,
            JournalDatabase.DATABASE_NAME
        ).build()
    }
    single {
        get<JournalDatabase>().journalDao
    }
    single<JournalRepository> {
        JournalRepositoryImpl(get())
    }
    single {
        JournalUseCases(
            getJournals = GetJournalsUseCase(get()),
            deleteJournal = DeleteJournalUseCase(get()),
            createJournal = CreateJournalUseCase(get()),
            getJournalById = GetJournalByIdUseCase(get())
        )
    }
    viewModelOf(::JournalHomeViewModel)
    viewModelOf(::JournalEditorViewModel)
    viewModel { (journalId: Int) ->
        JournalDetailsViewModel(get(), journalId)
    }
}